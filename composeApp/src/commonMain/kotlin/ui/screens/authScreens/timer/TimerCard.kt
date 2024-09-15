package ui.screens.authScreens.timer

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_play
import bauchglueck.composeapp.generated.resources.ic_stop
import bauchglueck.composeapp.generated.resources.icon_pause
import bauchglueck.composeapp.generated.resources.icon_sync
import data.local.entitiy.CountdownTimer
import data.local.entitiy.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import ui.components.DropDownMenu
import ui.components.DropdownMenuRow
import ui.components.clickableWithRipple
import ui.theme.AppTheme
import ui.theme.rodettaFontFamily
import util.formatTimeToMMSS

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun TestPreview(
    timer: CountdownTimer = CountdownTimer(
        name = "TimerName"
    )
) {
    AppTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
                ,
            ) {
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = timer.name,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = MaterialTheme.typography.headlineMedium.fontFamily,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }
}

@Composable
fun TimerCard(
    timer: CountdownTimer,
    onClickEdit: (CountdownTimer) -> Unit = {},
    onDelete: (CountdownTimer) -> Unit = {},
    onTimerUpdate: (CountdownTimer) -> Unit = {}
) {

    val timerControllerViewModel: TimerController = viewModel(
        key = "TimerController_${timer.timerId}",
        factory = viewModelFactory {
            initializer {
                TimerController(timer)
            }
        }
    )

    val remainingTime by timerControllerViewModel.remainingTime.collectAsState()
    val timerState by timerControllerViewModel.timerState.collectAsState()
    val startDate by timerControllerViewModel.startDate.collectAsState()
    val endDate by timerControllerViewModel.endDate.collectAsState()

    LaunchedEffect(timerState) {
        onTimerUpdate(
            timer.copy(
                startDate = startDate,
                endDate = endDate,
                timerState = timerState.value,
                updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
    ) {
       Column(
           modifier = Modifier,
           verticalArrangement = Arrangement.spacedBy(5.dp),
           horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Row(
               modifier = Modifier.fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.Top
           ) {
               Text(
                   modifier = Modifier.padding(top = 5.dp),
                   text = timer.name,
                   color = MaterialTheme.colorScheme.onBackground,
                   fontFamily = MaterialTheme.typography.headlineMedium.fontFamily,
                   fontWeight = FontWeight.Bold
               )

               DropDownMenu(
                   modifier = Modifier,
                   dropDownOptions = listOf(
                       DropdownMenuRow(
                           text = "Edit",
                           onClick = {  onClickEdit(timer)  },
                           leadingIcon = Icons.Outlined.Edit
                       ),
                       DropdownMenuRow(
                           text ="Delete",
                           onClick = { onDelete(timer) },
                           leadingIcon = Icons.Outlined.Delete
                       )
                   )
               )
           }

           Row(
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(bottom = 5.dp),
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.Bottom
           ) {

               when (timerState) {
                   TimerState.running -> {
                       Row(
                           horizontalArrangement = Arrangement.spacedBy(5.dp),
                           verticalAlignment = Alignment.CenterVertically
                       ) {
                           ControlButton(icon = Res.drawable.icon_pause) { timerControllerViewModel.pause() }
                           ControlButton(icon = Res.drawable.ic_stop) { timerControllerViewModel.stop() }
                       }
                   }
                   TimerState.paused -> ControlButton(icon = Res.drawable.ic_play) { timerControllerViewModel.resume() }
                   TimerState.completed -> ControlButton(icon = Res.drawable.icon_sync) { timerControllerViewModel.reset() }
                   TimerState.notRunning -> ControlButton(icon = Res.drawable.ic_play) { timerControllerViewModel.start() }
               }

               Text(
                   text = remainingTime.formatTimeToMMSS(),
                   color = MaterialTheme.colorScheme.onBackground,
                   fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                   fontWeight = FontWeight.SemiBold
               )
           }
       }
    }
}

@Composable
fun ControlButton(icon: DrawableResource, action: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                Color.White,
                CircleShape
            )
            .padding(5.dp)
            .clickableWithRipple {
                action()
            }
    ) {
        Icon(
            imageVector = vectorResource(icon),
            contentDescription = ""
        )
    }
}


class TimerController(
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
        val currentTime = Clock.System.now().toEpochMilliseconds()
        _startDate.value = currentTime
        _endDate.value = currentTime + timer.duration * 1000 // Convert duration to milliseconds
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