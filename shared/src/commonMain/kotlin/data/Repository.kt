package data

import data.local.LocalDataSource
import data.local.LocalDataSourceImpl
import data.local.LocalDatabase
import data.local.entitiy.CountdownTimer
import data.local.entitiy.SyncHistory
import data.remote.RemoteDataSource
import data.remote.model.CountdownTimerAttributes
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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
    var localDataSource: LocalDataSource = LocalDataSourceImpl(db)
    private var remoteDataSource: RemoteDataSource = RemoteDataSource(serverHost)
    private var firebase: Firebase = Firebase

    private val _repositoryUiState: MutableStateFlow<RepositoryUiState> = MutableStateFlow(RepositoryUiState())
    val repositoryUiState: StateFlow<RepositoryUiState>
        get() = _repositoryUiState

    suspend fun syncLocalTimer(delay: Long = 3000): Result<String, SyncError> {
        _repositoryUiState.update { it.copy(deviceID = deviceID, isLoading = true) }

        val user = firebase.auth.currentUser

        user?.let { user ->
            val localTimers = localDataSource.getAllTimer()

            remoteDataSource.countdownTimer.getCountdownTimers(user.uid).onSuccess { remoteTimer ->

                remoteTimer.forEach { remoteTimer ->
                     val localTimer = localTimers.find { it.timerId == remoteTimer.timerId }

                    if (localTimer == null) {
                        val newTimer = CountdownTimer(
                            timerId = remoteTimer.timerId,
                            userId = user.uid,
                            name = remoteTimer.name,
                            duration = remoteTimer.duration.toLongOrNull() ?: 0L,
                            startDate = remoteTimer.startDate?.toLongOrNull(),
                            endDate = remoteTimer.endDate?.toLongOrNull(),
                            timerState = remoteTimer.timerState,
                            showActivity = remoteTimer.showActivity ?: true,
                            createdAt = Instant.parse(remoteTimer.createdAt).toEpochMilliseconds(),
                            updatedAt = Instant.parse(remoteTimer.updatedAt).toEpochMilliseconds()
                        )
                        localDataSource.insertTimer(newTimer)
                        _repositoryUiState.update {
                            it.copy(currentTimer = localDataSource.getAllTimer())
                        }
                    } else {


                        localTimer.name = remoteTimer.name
                        localTimer.duration = remoteTimer.duration.toLongOrNull() ?: 0L
                        localTimer.startDate = remoteTimer.startDate?.toLongOrNull()
                        localTimer.endDate = remoteTimer.endDate?.toLongOrNull()
                        localTimer.timerState = remoteTimer.timerState
                        localTimer.showActivity = remoteTimer.showActivity
                        localTimer.updatedAt = remoteTimer.updatedAt.toLongOrNull()

                        localDataSource.updateTimer(localTimer)
                        _repositoryUiState.update {
                            it.copy(currentTimer = localDataSource.getAllTimer())
                        }
                    }
                 }
                return Result.Success("Local: ${localTimers.size}, Remote: ${remoteTimer.size}")
            }.onError {
                return Result.Success("Local: ${localTimers.size}, Error: ${it.name}")
            }
        }

        return Result.Success("Local: ${_repositoryUiState.value.currentTimer.size}")
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



private fun createNewTimerFromApi(apiTimer: CountdownTimerAttributes, apiUpdatedAt: Long): CountdownTimer {
    return CountdownTimer(
        timerId = apiTimer.timerId,
        userId = apiTimer.userId,
        name = apiTimer.name,
        duration = apiTimer.duration.toLongOrNull() ?: 0L,
        startDate = apiTimer.startDate?.toLongOrNull(),
        endDate = apiTimer.endDate?.toLongOrNull(),
        timerState = apiTimer.timerState,
        showActivity = apiTimer.showActivity ?: true,
        createdAt = Instant.parse(apiTimer.createdAt).toEpochMilliseconds(),
        updatedAt = apiUpdatedAt
    )
}

private fun updateLocalTimerWithApiData(localTimer: CountdownTimer, apiTimer: CountdownTimerAttributes) {
    localTimer.apply {
        name = apiTimer.name
        duration = apiTimer.duration.toLongOrNull() ?: 0L
        startDate = apiTimer.startDate?.toLongOrNull()
        endDate = apiTimer.endDate?.toLongOrNull()
        timerState = apiTimer.timerState
    }
}