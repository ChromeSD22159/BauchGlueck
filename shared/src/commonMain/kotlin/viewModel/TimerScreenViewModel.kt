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
): ViewModel(), BaseViewModel<TimerScreenUiState, CountdownTimer> {
    override val scope: CoroutineScope = viewModelScope

    private val _uiState = MutableStateFlow(TimerScreenUiState())
    override val uiState: StateFlow<TimerScreenUiState> = _uiState.asStateFlow()


    private val _selectedTimer: MutableStateFlow<CountdownTimer?> = MutableStateFlow(null)
    val selectedTimer: StateFlow<CountdownTimer?> = _selectedTimer.asStateFlow()

    init {
        logging().info { "TimerViewModel init" }
        scope.launch(Dispatchers.IO) {
            getAllItems()
        }
    }

    override fun onCleared() {
        super.onCleared()
        logging().info { "TimerViewModel onCleared" }
    }

    override fun getAllItems() {
        try {
            val items = repository.countdownTimerRepository.getAll()
            _uiState.update {
                it.copy(items = items)
            }
        } catch (e: Exception) {
            logging().error { "Error loading items: ${e.message}" }
        }
    }

    override fun addItem(item: CountdownTimer) {
        scope.launch {
            repository.countdownTimerRepository.insertOrUpdate(item.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))

            _uiState.update {
                it.copy(
                    items = repository.countdownTimerRepository.getAll(),
                )
            }

            syncDataWithRemote()
        }
    }

    override fun updateItemAndSyncRemote(item: CountdownTimer) {
        scope.launch {
            repository.countdownTimerRepository.insertOrUpdate(item.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))

            syncDataWithRemote()

            getAllItems()
        }
    }

    override fun softDelete(item: CountdownTimer) {
        scope.launch {
            val timer = item.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds())
            repository.countdownTimerRepository.softDeleteMany(listOf(timer))
            _uiState.update {
                it.copy(
                    items = repository.countdownTimerRepository.getAll(),
                )
            }

            syncDataWithRemote()
        }
    }

    override fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.countdownTimerRepository.syncDataWithRemote()
            getAllItems()
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

data class TimerScreenUiState(
    override var isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val isFinishedSyncing: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val minimumDelay: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val hasError: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override var items: Flow<List<CountdownTimer>> = emptyFlow(),
    var selectedTimer: MutableStateFlow<CountdownTimer?> = MutableStateFlow(null),
): BaseUiState<CountdownTimer>

