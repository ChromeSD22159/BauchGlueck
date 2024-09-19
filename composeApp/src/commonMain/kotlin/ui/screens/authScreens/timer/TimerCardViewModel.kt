package ui.screens.authScreens.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.local.entitiy.CountdownTimer
import data.local.entitiy.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class TimerCardViewModel(
    private var timer: CountdownTimer
) : ViewModel() {

    private val scope = viewModelScope
    private val _remainingTime = MutableStateFlow(timer.duration) // duration in seconds
    val remainingTime = _remainingTime.asStateFlow()

    private val _timerState = MutableStateFlow(timer.toTimerState)
    val timerState = _timerState.asStateFlow()

    private val _startDate = MutableStateFlow(timer.startDate)
    val startDate = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow(timer.endDate)
    val endDate = _endDate.asStateFlow()

    private var job: Job? = null

    init {
        when (timer.toTimerState) {
            TimerState.running -> {
                timer.endDate?.let { endDate ->
                    val currentTime = Clock.System.now().toEpochMilliseconds()
                    if (endDate >= currentTime) {
                        _remainingTime.value = ((endDate - currentTime) / 1000).coerceAtLeast(0)
                        _timerState.value = TimerState.running
                        startTicking()
                    } else {
                        completeInternal()
                    }
                } ?: run {
                    // If endDate is null, consider the timer not running
                    _timerState.value = TimerState.notRunning
                }
            }
            TimerState.paused -> {
                // The timer is paused; _remainingTime is already set
                _timerState.value = TimerState.paused
            }
            TimerState.completed -> {
                _remainingTime.value = 0
                _timerState.value = TimerState.completed
            }
            TimerState.notRunning -> {
                _remainingTime.value = timer.duration
                _timerState.value = TimerState.notRunning
            }
        }
    }

    fun start() {
        val startTimeStampUtc = Clock.System.now().toEpochMilliseconds()
        val endTimeStampUtc = startTimeStampUtc + timer.duration
        _startDate.value = startTimeStampUtc
        _endDate.value = endTimeStampUtc + (timer.duration * 1000)
        _remainingTime.value = timer.duration
        _timerState.value = TimerState.running
        startTicking()
    }

    private fun startTicking() {
        job?.cancel() // Cancel previous job
        job = scope.launch {
            while (isActive) {
                delay(1000)
                _remainingTime.value -= 1 // Decrement by 1 second
                if (_remainingTime.value <= 0) {
                    completeInternal()
                    break
                }
            }
        }
    }

    fun resume() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        _startDate.value = currentTime
        _endDate.value = currentTime + _remainingTime.value * 1000 // Remaining time in seconds to milliseconds
        _timerState.value = TimerState.running
        startTicking()
    }

    fun stop() {
        _timerState.value = TimerState.notRunning
        _remainingTime.value = 0
        job?.cancel()
    }

    fun pause() {
        _timerState.value = TimerState.paused
        job?.cancel()
    }

    fun reset() {
        _startDate.value = null
        _endDate.value = null
        _remainingTime.value = timer.duration
        _timerState.value = TimerState.notRunning
        job?.cancel()
    }

    private fun completeInternal() {
        _remainingTime.value = 0
        _timerState.value = TimerState.completed
        job?.cancel()
    }
}