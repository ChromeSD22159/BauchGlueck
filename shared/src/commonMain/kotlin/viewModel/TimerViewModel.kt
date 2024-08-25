package viewModel

import data.Repository
import data.local.entitiy.CountdownTimer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import model.countdownTimer.TimerState
import org.lighthousegames.logging.logging
import util.onError
import util.onSuccess
import util.toIsoDate

class TimerViewModel(
    private val repository: Repository
): ViewModel() {

    private var _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    init {
        logging().info { "TimerViewModel init" }
        getAllCountdownTimers()
    }

    override fun onCleared() {
        super.onCleared()
        logging().info { "TimerViewModel onCleared" }
    }

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

    fun addTimer(name: String, duration: Long) {
        val newTimer = CountdownTimer(
            name = name,
            duration = duration,
            timerState = TimerState.notRunning.name,
            isDeleted = false,
            createdAt = Clock.System.now().toEpochMilliseconds().toIsoDate(),
            updatedAt = Clock.System.now().toEpochMilliseconds().toIsoDate()
        )

        viewModelScope.launch {
            repository.countdownTimerRepository.insertOrUpdate(newTimer)
            _uiState.value = _uiState.value.copy(timer = repository.countdownTimerRepository.getAll())
        }
    }

    fun updateLocalData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.countdownTimerRepository.updateLocalData()
                    .onSuccess { list ->
                        logging().info { "updateLocalData: ${list.size}" }
                    }
                    .onError {
                        logging().error { "updateLocalError: ${it.name}" }
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
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

    fun updateTimer(countdownTimer: CountdownTimer) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.countdownTimerRepository.insertOrUpdate(countdownTimer)
            _uiState.value = _uiState.value.copy(timer = repository.countdownTimerRepository.getAll())
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun softDeleteTimer(countdownTimer: CountdownTimer) {
        viewModelScope.launch {
            val timer = countdownTimer.copy(isDeleted = true, updatedAt = Clock.System.now().toEpochMilliseconds().toIsoDate())
            repository.countdownTimerRepository.softDeleteMany(listOf(timer))
            updateRemoteData()
            _uiState.value = _uiState.value.copy(timer = repository.countdownTimerRepository.getAll())
        }
    }

    suspend fun test(lastSync: Long): List<CountdownTimer> {
        val user = Firebase.auth.currentUser
        return repository.countdownTimerRepository.localService.getAllAfterTimeStamp(lastSync, user!!.uid)
    }
}

data class TimerUiState(
    var isLoading: Boolean = false,
    var timer: List<CountdownTimer> = emptyList(),
    var error: String? = null
)

