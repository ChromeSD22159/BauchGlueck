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
            _uiState.value.timers.value = repository.countdownTimerRepository.getAll()
            syncDataWithRemote()
        }
    }

    fun getAllCountdownTimers() {
        viewModelScope.launch {
            _uiState.value.isLoading.value = true
            _uiState.value.timers.value = repository.countdownTimerRepository.getAll()
            _uiState.value.isLoading.value = false
        }
    }

    fun updateTimerAndSyncRemote(countdownTimer: CountdownTimer) {
        viewModelScope.launch {
            logging().info { "updateTimer: $countdownTimer" }
            repository.countdownTimerRepository.insertOrUpdate(countdownTimer)
            _uiState.value.timers.value = repository.countdownTimerRepository.getAll()
            syncDataWithRemote()
            clearSelectedTimer()
        }
    }

    fun softDeleteTimer(countdownTimer: CountdownTimer) {
        viewModelScope.launch {
            val timer = countdownTimer.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds())
            repository.countdownTimerRepository.softDeleteMany(listOf(timer))
            _uiState.value.timers.value = repository.countdownTimerRepository.getAll()
            syncDataWithRemote()
        }
    }

    fun setSelectedTimer(countdownTimer: CountdownTimer) {
        _uiState.value.selectedTimer.value = countdownTimer
    }

    private fun clearSelectedTimer() {
        _uiState.value.selectedTimer.value = null
    }

    fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.countdownTimerRepository.syncDataWithRemote()
        }
    }
}

data class TimerUiState(
    var isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false),
    var selectedTimer: MutableStateFlow<CountdownTimer?> = MutableStateFlow(null),
    var timers: MutableStateFlow<List<CountdownTimer>> =  MutableStateFlow(emptyList()),
    var error: MutableStateFlow<String?> = MutableStateFlow(null)
)

