package data

import data.local.LocalDataSource
import data.local.LocalDataSourceImpl
import data.local.LocalDatabase
import data.local.entitiy.CountdownTimer
import data.local.entitiy.SyncHistory
import data.remote.RemoteDataSource
import data.remote.model.TimerData
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import util.Error
import util.Result
import util.onError
import util.onSuccess

class Repository(
    db: LocalDatabase,
    serverHost: String,
    var deviceID: String
) {
    private var localDataSource: LocalDataSource = LocalDataSourceImpl(db)
    private var remoteDataSource: RemoteDataSource = RemoteDataSource(serverHost)
    private var firebase: Firebase = Firebase

    private val _repositoryUiState: MutableStateFlow<RepositoryUiState> = MutableStateFlow(RepositoryUiState())
    val repositoryUiState: StateFlow<RepositoryUiState>
        get() = _repositoryUiState

    suspend fun syncLocalTimer(delay: Long = 3000) {
        _repositoryUiState.update { it.copy(deviceID = deviceID, isLoading = true) }

        // 1. Load all Local Timers in uiState
        val localTimers = localDataSource.getAllTimer()

        // 2. Load Remote Timers
        val remoteData = remoteDataSource.countdownTimer.getCountdownTimers(userID = firebase.auth.currentUser?.uid ?: "")

        remoteData.onSuccess { timerResponse ->
            val timersFromApi = timerResponse.data

            // 3. Sync logic - Check if data is identical, otherwise update the data in the DB
            timersFromApi.forEach { apiTimer ->
                val localTimer = localTimers.find { it.timerId == apiTimer.id.toString() }
                val apiUpdatedAt = Instant.parse(apiTimer.attributes.updatedAt).toEpochMilliseconds()

                if (localTimer == null) {
                    // Insert new timer into db
                    val newTimer = createNewTimerFromApi(apiTimer, apiUpdatedAt)
                    localDataSource.insertTimer(newTimer)
                } else if (localTimer.updatedAt?.let { it < apiUpdatedAt } == true) {
                    // Update existing timer only if remote data is newer
                    updateLocalTimerWithApiData(localTimer, apiTimer)
                    localDataSource.updateTimer(localTimer)
                }
            }

            // Update UI State and add delay
            _repositoryUiState.update {
                it.copy(isLoading = false, currentTimer = localTimers + localDataSource.getAllTimer())
            }

            delay(delay)

            // Insert Sync History
            localDataSource.insertSyncHistory(
                SyncHistory(
                    deviceId = deviceID,
                    lastSync = Clock.System.now().toEpochMilliseconds()
                )
            )
        }.onError { error ->
            _repositoryUiState.update { it.copy(isLoading = false, error = error.name) }
            delay(delay)
        }
    }

    suspend fun syncRemoteTimer(): Result<String, Repository.SyncError> {

        return try {
            if (deviceID.isEmpty()) return Result.Error(SyncError.NoDeviceId)

            val lastSync = localDataSource.getLastSyncEntry(deviceID) ?: SyncHistory(deviceId = deviceID, lastSync = 0)

            _repositoryUiState.update { it.copy(isLoading = true, lastSync = lastSync.lastSync) }

            val localDataToUpdate = if (lastSync.lastSync == 0L) {
                localDataSource.getAllTimer()
            } else {
                localDataSource.getEntriesSinceLastUpdate(lastSync.lastSync)
            }

            if (localDataToUpdate.isEmpty()) return Result.Success("0")

            remoteDataSource.countdownTimer.updateOrInsertCountdownTimers(localDataToUpdate)

            localDataSource.deleteAllSyncHistory()

            localDataSource.insertSyncHistory(
                SyncHistory(
                    deviceId = deviceID,
                    lastSync = Clock.System.now().toEpochMilliseconds()
                )
            )

            Result.Success(localDataToUpdate.toString())
        } catch (e: Exception) {
           Result.Error(SyncError.Unknown)
        } finally {
            _repositoryUiState.update { it.copy(isLoading = false) }
        }
    }

    data class RepositoryUiState(
        val isLoading: Boolean = false,
        var deviceID: String = "",
        var currentTimer: List<CountdownTimer> = emptyList(),
        val error: String? = null,
        val lastSync: Long = 0
    )

    enum class SyncError: Error {
        Synced,
        NotSynced,
        LastSyncTimestampNotFound,
        NoUserFound,
        NoDeviceId,
        Unknown,
        RemoteTimerNotFound
    }
}



private fun createNewTimerFromApi(apiTimer: TimerData, apiUpdatedAt: Long): CountdownTimer {
    return CountdownTimer(
        timerId = apiTimer.id.toString(),
        userId = apiTimer.attributes.userId,
        name = apiTimer.attributes.name,
        duration = apiTimer.attributes.duration.toLongOrNull() ?: 0L,
        startDate = apiTimer.attributes.startDate?.toLongOrNull(),
        endDate = apiTimer.attributes.endDate?.toLongOrNull(),
        timerState = apiTimer.attributes.timerState,
        showActivity = apiTimer.attributes.showActivity ?: true,
        createdAt = Instant.parse(apiTimer.attributes.createdAt).toEpochMilliseconds(),
        updatedAt = apiUpdatedAt
    )
}

private fun updateLocalTimerWithApiData(localTimer: CountdownTimer, apiTimer: TimerData) {
    localTimer.apply {
        name = apiTimer.attributes.name
        duration = apiTimer.attributes.duration.toLongOrNull() ?: 0L
        startDate = apiTimer.attributes.startDate?.toLongOrNull()
        endDate = apiTimer.attributes.endDate?.toLongOrNull()
        timerState = apiTimer.attributes.timerState
    }
}