package viewModel

import data.Repository
import data.local.entitiy.CountdownTimer
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging

class TimerViewModel(
    private val repository: Repository
): ViewModel() {
    val scope: CoroutineScope = viewModelScope

    private val _uiState = MutableStateFlow(TimerScreenUiState())
    val uiState: StateFlow<TimerScreenUiState> = _uiState.asStateFlow()

    private val _selectedTimer: MutableStateFlow<CountdownTimer?> = MutableStateFlow(null)
    val selectedTimer: StateFlow<CountdownTimer?> = _selectedTimer.asStateFlow()

    init {
        logging().info { "TimerViewModel init" }
        _uiState.value = _uiState.value.copy(items = repository.countdownTimerRepository.getAll())
    }

    override fun onCleared() {
        super.onCleared()
        logging().info { "TimerViewModel onCleared" }
    }

    fun addItem(item: CountdownTimer) {
        scope.launch {
            repository.countdownTimerRepository.insertOrUpdate(item.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))
            _uiState.value = _uiState.value.copy(items = repository.countdownTimerRepository.getAll())

            syncDataWithRemote()
        }
    }

    fun updateItemAndSyncRemote(item: CountdownTimer) {
        scope.launch {
            repository.countdownTimerRepository.insertOrUpdate(item.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))
            _uiState.value = _uiState.value.copy(items = repository.countdownTimerRepository.getAll()) // Update uiState
            syncDataWithRemote()
        }
    }

    fun softDelete(item: CountdownTimer) {
        scope.launch {
            val timer = item.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds())
            repository.countdownTimerRepository.softDeleteMany(listOf(timer))
            _uiState.value = _uiState.value.copy(items = repository.countdownTimerRepository.getAll())

            syncDataWithRemote()
        }
    }

    private fun clearSelectedTimer() {
        _selectedTimer.value = null
    }

    fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.countdownTimerRepository.syncDataWithRemote()
        }
    }

    fun setSelectedTimer(countdownTimer: CountdownTimer) {
        _selectedTimer.value = countdownTimer
    }


    fun updateTimerWhileRunning(countdownTimer: CountdownTimer) {
        viewModelScope.launch {
            repository.countdownTimerRepository.insertOrUpdate(countdownTimer)
        }
    }
}

data class TimerScreenUiState(
    var isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val isFinishedSyncing: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val minimumDelay: MutableStateFlow<Boolean> = MutableStateFlow(false),
    val hasError: MutableStateFlow<Boolean> = MutableStateFlow(false),
    var items: Flow<List<CountdownTimer>> =  emptyFlow(),
    var selectedTimer: MutableStateFlow<CountdownTimer?> = MutableStateFlow(null),
)
    //: BaseUiState<CountdownTimer>

