package viewModel

import com.mmk.kmpnotifier.notification.NotifierManager
import data.Repository
import data.local.entitiy.CountdownTimer
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import util.FirebaseFunctionsCronJobManager
import util.NotificationCronJobRequest
import util.NotificationDetails
import util.onError
import util.onSuccess
import util.toUTC

class TimerScreenViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val scope: CoroutineScope = viewModelScope

    private var _allTimers: MutableStateFlow<List<CountdownTimer>> = MutableStateFlow(emptyList())
    val x: StateFlow<List<CountdownTimer>> = _allTimers.asStateFlow()

    private val _selectedTimer: MutableStateFlow<CountdownTimer?> = MutableStateFlow(null)
    val selectedTimer: StateFlow<CountdownTimer?> = _selectedTimer.asStateFlow()

    val allTimers = repository.countdownTimerRepository.getAllAsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), emptyList())

    init {
        loadAllTimers()
    }

    override fun onCleared() {
        super.onCleared()
        logging().info { "TimerViewModel onCleared" }
    }

    fun loadAllTimers() {
        scope.launch {
            repository.countdownTimerRepository.getAllAsFlow().collect {
                _allTimers.value = it
            }
        }
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

    fun sendScheduleRemoteNotification(timer: CountdownTimer) {
        viewModelScope.launch {
            val token = NotifierManager.getPushNotifier().getToken() ?: return@launch
            val endDate = timer.endDate ?: return@launch
            val now = Clock.System.now().toEpochMilliseconds()



            if (endDate > now) {
                val newNotification = NotificationDetails(
                    token = token,
                    title = "BauchGlück Notification",
                    body = "Dein ${timer.name} ist abgelaufen!"
                )

                val job = FirebaseFunctionsCronJobManager.generateCronJob(
                    identifier = timer.timerId,
                    oneTimeSchedule = endDate.toUTC()
                )

                val request = NotificationCronJobRequest(
                    notification = newNotification,
                    cronJob = job,
                )

                val res = repository.firebaseRepository.sendScheduleRemoteNotification(request)

                res.onSuccess {
                    logging().info { "Notification sent: $it" }
                }.onError {
                    logging().info { "Notification failed: $it" }
                }
            }
        }
    }
}