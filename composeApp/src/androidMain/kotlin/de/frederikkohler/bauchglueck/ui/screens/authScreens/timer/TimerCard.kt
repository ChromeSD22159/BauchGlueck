package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import data.local.entitiy.CountdownTimer
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import model.countdownTimer.TimerState
import util.DateConverter
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Job
import viewModel.TimerViewModel

@Composable
fun TimerCard(
    timer: CountdownTimer,
    viewModel: TimerViewModel
) {

    val thisCountdownViewModel: CountdownViewModel = viewModel(
        key = timer.id.toString(),
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CountdownViewModel(timer, viewModel) as T
            }
        }
    )

    val remainingTime by thisCountdownViewModel::remainingTime
    val timerState by thisCountdownViewModel::isRunning

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    //color = MaterialTheme.colorScheme.onTertiaryContainer,
                    text = DateConverter().formatTimeToMMSS(remainingTime)
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    // color = MaterialTheme.colorScheme.onSecondaryContainer,
                    text = timer.name
                )
            }

            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {

                val endTime = if (timer.endDate != null) {
                    timer.endDate!! - Clock.System.now().toEpochMilliseconds()
                } else {
                    0
                }

                CircularProgressIndicator(
                    progress = { (timer.duration - endTime) / timer.duration.toFloat() },
                    modifier = Modifier
                        .size(80.dp),
                    //color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 8.dp,
                )

                when (timerState) {
                    TimerState.running -> Text(text = "running", modifier = Modifier.clickable { thisCountdownViewModel.pauseCountdown() })
                    TimerState.paused -> Text(text = "paused", modifier = Modifier.clickable { thisCountdownViewModel.resumeCountdown() })
                    TimerState.completed -> Text(text = "completed", modifier = Modifier.clickable { thisCountdownViewModel.resetCountdown() })
                    TimerState.notRunning -> Text(text = "notRunning", modifier = Modifier.clickable { thisCountdownViewModel.startCountdown() })
                    else -> thisCountdownViewModel.pauseCountdown()
                }
            }
        }
    }
}

class CountdownViewModel(
    private val countdownTimer: CountdownTimer,
    private val timerViewModel: TimerViewModel
) : ViewModel() {
    var remainingTime by mutableLongStateOf(countdownTimer.duration)
    var isRunning by mutableStateOf(TimerState.notRunning)
    var startDateTime: Long? by mutableStateOf(null)
    var endDateTime: Long? by mutableStateOf(null)

    private var countdownJob: Job? = null

    init {
        isRunning = TimerState.valueOf(countdownTimer.timerState)
        startDateTime = countdownTimer.startDate
        endDateTime = countdownTimer.endDate

        // Berechnung der verbleibenden Zeit beim Initialisieren
        if (isRunning == TimerState.running && endDateTime != null) {
            val now = Clock.System.now().toEpochMilliseconds()
            remainingTime = endDateTime!! - now
            if (remainingTime > 0) {
                startCountdown() // Countdown neu starten
            } else {
                remainingTime = 0
                isRunning = TimerState.completed
                updateTimerInDatabase()
            }
        }

        if (isRunning == TimerState.notRunning) {
            remainingTime = countdownTimer.duration
        }
    }

    fun startCountdown() {
        if (isRunning == TimerState.running) return

        isRunning = TimerState.running
        startDateTime = Clock.System.now().toEpochMilliseconds()
        endDateTime = startDateTime!! + remainingTime

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (remainingTime > 0 && isRunning == TimerState.running) {
                delay(1000)
                remainingTime -= 1000L
                if (remainingTime <= 0) {
                    remainingTime = 0
                    isRunning = TimerState.completed
                }
                updateTimerInDatabase()
            }
        }
        updateTimerInDatabase() // Timer sofort aktualisieren, wenn er gestartet wird
    }

    fun pauseCountdown() {
        if (isRunning != TimerState.running) return

        isRunning = TimerState.paused
        countdownJob?.cancel()
        updateTimerInDatabase()
    }

    fun resumeCountdown() {
        if (countdownTimer.timerState == TimerState.paused.name) {
            startDateTime = Clock.System.now().toEpochMilliseconds()
            endDateTime = startDateTime!! + remainingTime
        } else {
            startDateTime = Clock.System.now().toEpochMilliseconds()
            endDateTime = startDateTime!! + countdownTimer.duration
            remainingTime = countdownTimer.duration
        }
        isRunning = TimerState.running
        startCountdown()
        updateTimerInDatabase()
    }

    fun resetCountdown() {
        isRunning = TimerState.notRunning
        remainingTime = countdownTimer.duration
        startDateTime = null
        endDateTime = null
        countdownJob?.cancel()
        updateTimerInDatabase()
    }

    private fun updateTimerInDatabase() {
        val updatedTimer = countdownTimer.copy(
            duration = remainingTime, // verbleibende Zeit speichern
            startDate = startDateTime,
            endDate = endDateTime,
            timerState = isRunning.name,
            updatedAt = Clock.System.now().toEpochMilliseconds()
        )
        viewModelScope.launch {
            timerViewModel.repository.countdownTimerLocalDataSource.updateTimer(updatedTimer)
        }
    }
}