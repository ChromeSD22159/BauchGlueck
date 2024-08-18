package data

import data.local.LocalDataSource
import data.local.LocalDataSourceImpl
import data.local.LocalDatabase
import data.local.entitiy.CountdownTimer
import data.remote.RemoteDataSource
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import util.onError
import util.onSuccess

class Repository(
    db: LocalDatabase,
    serverHost: String
) {
    private var localDataSource: LocalDataSource = LocalDataSourceImpl(db)
    private var remoteDataSource: RemoteDataSource = RemoteDataSource(serverHost)
    private var firebase: Firebase = Firebase

    private val _repositoryUiState: MutableStateFlow<RepositoryUiState> = MutableStateFlow(RepositoryUiState())
    val repositoryUiState: StateFlow<RepositoryUiState>
        get() = _repositoryUiState

    suspend fun getTimer(delay: Long = 3000) {
        // 1. Lade alle Local-Timer in uiState
        val localTimers = localDataSource.getAllTimer()


        // 2. Lade Remote-Timer
        _repositoryUiState.update { it.copy(isLoading = true, currentTimer = localTimers) }

        val remoteData = remoteDataSource.countdownTimer.getCountdownTimers(userID = firebase.auth.currentUser?.uid ?: "")

        remoteData.onSuccess { timerResponse ->
            val timersFromApi = timerResponse.data

            // 3. Überprüfe, ob die Daten identisch sind, ansonsten update die Daten in der DB
            timersFromApi.map { apiTimer ->
                val localTimer = localTimers.find { it.timerId == apiTimer.id.toString() }

                val apiUpdatedAt = Instant.parse(apiTimer.attributes.updatedAt).toEpochMilliseconds()

                if (localTimer == null) {
                    // insert new timer into db
                    val newTimer = CountdownTimer(
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

                    localDataSource.insertTimer(newTimer)
                } else {
                    // only update if remote data is newer
                    if (localTimer.updatedAt!! < apiUpdatedAt) {
                        localTimer.name = apiTimer.attributes.name
                        localTimer.duration = apiTimer.attributes.duration.toLongOrNull() ?: 0L
                        localTimer.startDate = apiTimer.attributes.startDate?.toLongOrNull()
                        localTimer.endDate = apiTimer.attributes.endDate?.toLongOrNull()
                        localTimer.timerState = apiTimer.attributes.timerState

                        localDataSource.updateTimer(localTimer)
                    }
                }
            }

            _repositoryUiState.update {
                delay(delay)
                it.copy(isLoading = false, currentTimer = localTimers + localDataSource.getAllTimer())
            }
        }

        remoteData.onError { error ->
            delay(delay)
            _repositoryUiState.update { it.copy(isLoading = false, error = error.name) }
        }
    }
}

data class RepositoryUiState(
    val isLoading: Boolean = false,
    var currentTimer: List<CountdownTimer> = emptyList(),
    val error: String? = null
)