package ui.screens.authScreens.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.google.common.primitives.Longs.max
import data.local.entitiy.CountdownTimer
import data.local.entitiy.TimerState
import de.frederikkohler.bauchglueck.R
import ui.components.clickableWithRipple
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import util.toIsoDate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimerCircle(
    countdownTimer: CountdownTimer,
    handleColor: Color,
    inactiveBarColor: Color,
    activeBarColor: Color,
    modifier: Modifier = Modifier,
    initialValue: Float = 1f,
    strokeWidth: Dp = 5.dp,
    remainingTime: (Long) -> Unit = {},
    onTick: (CountdownTimer) -> Unit = {}
) {

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    var value by remember {
        mutableFloatStateOf(initialValue)
    }
    var currentTime by remember {
        mutableLongStateOf(
            countdownTimer.endDate?.let {
                max(0, it - Clock.System.now().toEpochMilliseconds())
            } ?: (countdownTimer.duration * 1000)
        )
    }

    var isTimerRunning by remember {
        mutableStateOf(
            when (countdownTimer.timerState) {
                TimerState.running.name -> true
                else -> false
            }
        )
    }

    // Retrieve currentTime and isTimerRunning from SharedPreferences on app launch
    LaunchedEffect(Unit) {
        // Check if the timer should be completed
        if (isTimerRunning && currentTime <= 0L) {
            isTimerRunning = false
            currentTime = 0L
            countdownTimer.timerState = TimerState.completed.name
            countdownTimer.startDate = null
            countdownTimer.endDate = null
            onTick(countdownTimer.copy(updatedAt = Clock.System.now().toEpochMilliseconds().toIsoDate()))
        }
    }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            countdownTimer.startDate = Clock.System.now().toEpochMilliseconds()
            countdownTimer.endDate = Clock.System.now().toEpochMilliseconds() + currentTime
            countdownTimer.timerState = TimerState.running.name
        } else {
            countdownTimer.timerState = if (currentTime <= 0L) TimerState.completed.name else TimerState.paused.name

            if (currentTime <= 0L) {
                countdownTimer.startDate = null
                countdownTimer.endDate = null
            }
        }
    }

    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if(currentTime > 0 && isTimerRunning) {
            delay(100L)
            currentTime -= 100L
            value = currentTime / (countdownTimer.duration.toFloat() * 1000f)
            remainingTime(currentTime / 1000L)
        } else if (currentTime <= 0L && isTimerRunning) {
            // Timer completed, update state and reset dates
            isTimerRunning = false
            countdownTimer.timerState = TimerState.completed.name
            countdownTimer.startDate = null
            countdownTimer.endDate = null

            onTick(countdownTimer)
        }
    }

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .onSizeChanged {
                    size = it
                }
        ) {

            Canvas(
                modifier = modifier.clickableWithRipple {
                    if (currentTime <= 0L) {
                        currentTime = countdownTimer.duration * 1000
                        isTimerRunning = true
                        countdownTimer.timerState = TimerState.running.name
                        onTick(countdownTimer.copy(updatedAt = Clock.System.now().toEpochMilliseconds().toIsoDate()))
                    } else {
                        isTimerRunning = !isTimerRunning
                        countdownTimer.timerState = TimerState.completed.name

                        onTick(countdownTimer.copy(
                            startDate = null,
                            endDate = null,
                            timerState = TimerState.completed.name,
                            updatedAt = Clock.System.now().toEpochMilliseconds().toIsoDate())
                        )
                    }
                }
            ) {
                drawArc(
                    color = inactiveBarColor,
                    startAngle = -215f,
                    sweepAngle = 250f,
                    useCenter = false,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = activeBarColor,
                    startAngle = -215f,
                    sweepAngle = 250f * value,
                    useCenter = false,
                    size = Size(size.width.toFloat(), size.height.toFloat()),
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                )
                val center = Offset(size.width / 2f, size.height / 2f)
                val beta = (250f * value + 145f) * (PI / 180f).toFloat()
                val r = size.width / 2f
                val a = cos(beta) * r
                val b = sin(beta) * r
                drawPoints(
                    listOf(Offset((center.x + a), (center.y + b))),
                    pointMode = PointMode.Points,
                    color = handleColor,
                    strokeWidth = (strokeWidth * 3f).toPx(),
                    cap = StrokeCap.Round
                )
            }

            Icon(imageVector = ImageVector.vectorResource(
                id = when (countdownTimer.timerState) {
                    TimerState.running.name -> R.drawable.ic_stop
                    TimerState.paused.name -> R.drawable.icon_pause
                    TimerState.completed.name -> R.drawable.icon_sync
                    else -> R.drawable.ic_play
                }
            ), contentDescription = "Play/Pause")

        }
    }
}