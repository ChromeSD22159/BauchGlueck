package viewModel

import data.Repository
import data.local.entitiy.CountdownTimer
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import util.onError
import util.onSuccess

class TimerViewModel(
    private val repository: Repository
): ViewModel() {

    private var _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    fun updateRemoteData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.countdownTimerRepository.updateRemoteData()
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }

            _uiState.value.timer = repository.countdownTimerRepository.getAll()
        }
    }

    fun updateLocalData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.countdownTimerRepository.updateLocalData()
                    .onSuccess { list ->
                        _uiState.value = _uiState.value.copy(timer = list)
                    }
                    .onError {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _uiState.value = _uiState.value.copy(error = it.name)
                    }
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun getAllCountdownTimers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            _uiState.value = _uiState.value.copy(timer = repository.countdownTimerRepository.getAll())
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}

data class TimerUiState(
    var isLoading: Boolean = false,
    var timer: List<CountdownTimer> = emptyList(),
    var error: String? = null
)

