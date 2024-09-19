package viewModel

import com.mmk.kmpnotifier.notification.NotifierManager
import data.Repository
import data.local.entitiy.CountdownTimer
import data.model.RemoteNotification
import data.model.RemoteNotificationData
import data.model.ScheduleRemoteNotification
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import util.toIsoDate

class TimerScreenViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val scope: CoroutineScope = viewModelScope

    var allTimers: Flow<List<CountdownTimer>> = repository.countdownTimerRepository.getAll()

    private val _selectedTimer: MutableStateFlow<CountdownTimer?> = MutableStateFlow(null)
    val selectedTimer: StateFlow<CountdownTimer?> = _selectedTimer.asStateFlow()

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

    fun sendScheduleRemoteNotification(timer: CountdownTimer) {
        viewModelScope.launch {
            val token = NotifierManager.getPushNotifier().getToken() ?: return@launch
            val endDate = timer.endDate ?: return@launch
            val now = Clock.System.now().toEpochMilliseconds()

            if (endDate > now) {
                val notificationDate = endDate - now

                val newNotification = ScheduleRemoteNotification(
                    token = token,
                    body = "Timer",
                    title = "Timer",
                    data = RemoteNotificationData(
                        key1 = timer.id.toString(),
                        key2 = timer.name,
                    ),
                    scheduledTime = notificationDate.toIsoDate()
                )

                repository.firebaseRepository.sendScheduleRemoteNotification(newNotification)
            }
        }
    }

    fun sendNotification(timer: CountdownTimer) {
        viewModelScope.launch {
            val token = NotifierManager.getPushNotifier().getToken() ?: return@launch

            val newNotification = RemoteNotification(
                token = token,
                body = "Dein \"${timer.name}\" Timer wurde gestartet!",
                title = "Timer gestartet!",
                data = RemoteNotificationData(
                    key1 = timer.id.toString(),
                    key2 = timer.name,
                ),
            )

            repository.firebaseRepository.sendRemoteNotification(newNotification)
        }
    }
}

