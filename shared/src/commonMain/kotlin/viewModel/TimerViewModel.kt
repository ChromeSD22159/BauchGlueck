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
import util.generateDeviceId
import util.toIsoDate

class TimerViewModel(
    private val repository: Repository
): ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    init {
        logging().info { "TimerViewModel init" }
        getAllCountdownTimers()
    }

    override fun onCleared() {
        super.onCleared()
        logging().info { "TimerViewModel onCleared" }
    }

    fun addTimer(name: String, durationInMinutes: Long) {

        val newTimer = CountdownTimer(
            timerId = generateDeviceId(),
            userId = Firebase.auth.currentUser!!.uid,
            name = name,
            duration = durationInMinutes * 60,
            timerState = TimerState.notRunning.name,
            isDeleted = false,
            createdAt = Clock.System.now().toEpochMilliseconds().toIsoDate(),
            updatedAt = Clock.System.now().toEpochMilliseconds().toIsoDate()
        )

        viewModelScope.launch {
            repository.countdownTimerRepository.insertOrUpdate(newTimer)
            _uiState.value = _uiState.value.copy(timer = repository.countdownTimerRepository.getAll())
            syncDataWithRemote()
        }
    }

    fun getAllCountdownTimers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val timers = repository.countdownTimerRepository.getAll()
            timers.forEach {
                //logging().info { "getAllCountdownTimers: ${it.name} ${it.isDeleted}" }
            }
            _uiState.value = _uiState.value.copy(timer = timers)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun updateTimerAndSyncRemote(countdownTimer: CountdownTimer) {
        viewModelScope.launch {
            logging().info { "updateTimer: $countdownTimer" }
            repository.countdownTimerRepository.insertOrUpdate(countdownTimer)
            _uiState.value = _uiState.value.copy(timer = repository.countdownTimerRepository.getAll())
            syncDataWithRemote()
            clearSelectedTimer()
        }
    }

    fun softDeleteTimer(countdownTimer: CountdownTimer) {
        viewModelScope.launch {
            val timer = countdownTimer.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds())
            repository.countdownTimerRepository.softDeleteMany(listOf(timer))
            _uiState.value = _uiState.value.copy(timer = repository.countdownTimerRepository.getAll())
            syncDataWithRemote()
        }
    }

    fun setSelectedTimer(countdownTimer: CountdownTimer) {
        _uiState.value = _uiState.value.copy(selectedTimer = countdownTimer)
    }

    private fun clearSelectedTimer() {
        _uiState.value = _uiState.value.copy(selectedTimer = null)
    }

    fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.countdownTimerRepository.syncDataWithRemote()
        }
    }
}

data class TimerUiState(
    var isLoading: Boolean = false,
    var selectedTimer: CountdownTimer? = null,
    var timer: List<CountdownTimer> = emptyList(),
    var error: String? = null
)

