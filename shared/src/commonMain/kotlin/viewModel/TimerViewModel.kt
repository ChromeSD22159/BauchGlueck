package viewModel

import data.Repository
import data.local.entitiy.CountdownTimer
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel(
    val repository: Repository
): ViewModel() {
    private var _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    init {
        syncDataWithServer()
    }

    fun syncDataWithServer() {
        viewModelScope.launch {
            try {
                _uiState.value.isLoading = true
                repository.countdownTimerRepository.syncDataWithServer()
                _uiState.value.isLoading = false
            } catch (e: Exception) {
                _uiState.value.isLoading = false
            }

            _uiState.value.timer = repository.countdownTimerRepository.getAllCountdownTimers()
        }
    }

    data class TimerUiState(
        var isLoading: Boolean = false,
        var timer: List<CountdownTimer> = emptyList()
    )
}

