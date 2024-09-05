package viewModel

import data.Repository
import data.local.entitiy.CountdownTimer
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging

class TimerScreenViewModel(
    private val repository: Repository
): ViewModel() {
    private val scope: CoroutineScope = viewModelScope

    var allTimers: Flow<List<CountdownTimer>> = repository.countdownTimerRepository.getAll()

    private val _selectedTimer: MutableStateFlow<CountdownTimer?> = MutableStateFlow(null)
    val selectedTimer: StateFlow<CountdownTimer?> = _selectedTimer.asStateFlow()

    init {
        logging().info { "TimerViewModel init" }
    }

    override fun onCleared() {
        super.onCleared()
        logging().info { "TimerViewModel onCleared" }
    }

    fun addItem(item: CountdownTimer) {
        scope.launch {
            repository.countdownTimerRepository.insertOrUpdate(item.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))

            syncDataWithRemote()
        }
    }

    fun updateItemAndSyncRemote(item: CountdownTimer) {
        scope.launch {
            repository.countdownTimerRepository.insertOrUpdate(item.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))

            syncDataWithRemote()
        }
    }

    fun softDelete(item: CountdownTimer) {
        scope.launch {
            val timer = item.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds())
            repository.countdownTimerRepository.softDeleteMany(listOf(timer))

            syncDataWithRemote()
        }
    }

    fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.countdownTimerRepository.syncDataWithRemote()
        }
    }

    fun getTimerByIdOrNull(timerId: String?) {
        if (timerId == null) return
        viewModelScope.launch {
            _selectedTimer.emit(repository.countdownTimerRepository.getById(timerId))

            logging().info { "selectedTimer: ${_selectedTimer.value}" }
        }
    }

    fun updateTimerWhileRunning(countdownTimer: CountdownTimer) {
        viewModelScope.launch {
            repository.countdownTimerRepository.insertOrUpdate(countdownTimer)
        }
    }
}

