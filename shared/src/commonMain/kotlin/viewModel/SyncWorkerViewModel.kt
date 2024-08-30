package viewModel

import data.Repository
import data.network.isServerReachable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging


class SyncWorkerViewModel(
    private val repository: Repository,
): ViewModel() {
    private val scope = viewModelScope

    private var _uiState: MutableStateFlow<SyncWorkerUiState> = MutableStateFlow(SyncWorkerUiState())
    val uiState: StateFlow<SyncWorkerUiState> = _uiState.asStateFlow()

    private suspend fun syncData() {
        minimumDelay()
        scope.launch {
            val isReachable = isServerReachable()

            isReachable.onSuccess {
                logging().info { it }

                _uiState.value.isFinishedSyncing.value = false

                repository.firebaseRepository.user?.let {
                    repository.countdownTimerRepository.syncDataWithRemote()
                    repository.weightRepository.syncDataWithRemote()
                }

                _uiState.value.isFinishedSyncing.value = true
                _uiState.value.hasError.value = false
            }

            isReachable.onFailure { error ->
                logging().error { error.message }
                _uiState.value.hasError.value = true
                _uiState.value.isFinishedSyncing.value = true
            }
        }
    }

    private fun minimumDelay() {
        scope.launch {
            _uiState.value.minimumDelay.value = false
            delay(1000)
            _uiState.value.minimumDelay.value = true
        }
    }

    init {
        scope.launch {
            syncData()
        }
    }
}

data class SyncWorkerUiState(
    val isFinishedSyncing: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val minimumDelay: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val hasError: MutableStateFlow<Boolean> = MutableStateFlow(false),
)