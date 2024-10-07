package ui.screens.authScreens.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_timer
import data.local.entitiy.CountdownTimer
import data.local.entitiy.TimerState
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.SliderItemAddCard
import ui.components.theme.clickableWithRipple
import ui.components.extentions.sectionShadow
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import ui.screens.authScreens.timer.TimerCardViewModel
import util.DateConverter

@Composable
fun HomeTimerWidget(
    timers: List<CountdownTimer>,
    horizontalSpacing: Dp = 10.dp,
    itemSpacing: Dp = 8.dp,
    onNavigate: (Destination) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_add_timer),
                contentDescription = "icon",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.width(8.dp))

            FooterText("Timer", color = MaterialTheme.colorScheme.onBackground)

            Spacer(modifier = Modifier.weight(1f))
        }

        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(Modifier.width(horizontalSpacing - itemSpacing))

            val sorted = timers.sortedBy { TimerState.fromValue(it.timerState).state  }
            sorted.forEach { countDownTimer ->
                HomeCountdownTimerWidgetCard(countDownTimer) { onNavigate(it) }
            }

            if(sorted.isEmpty()) {
                NoTimerCard {
                    onNavigate(it)
                }
            }

            SliderItemAddCard(Destination.AddTimer) {
                onNavigate(it)
            }

            Spacer(Modifier.width(horizontalSpacing - itemSpacing))
        }
    }
}

@Composable
fun HomeCountdownTimerWidgetCard(
    timer: CountdownTimer,
    onNavigate: (Destination) -> Unit
) {
    val timerCardViewModelViewModel: TimerCardViewModel = viewModel(
        key = "TimerController_${timer.timerId}",
        factory = viewModelFactory {
            initializer {
                TimerCardViewModel(timer)
            }
        }
    )

    val remainingTime by timerCardViewModelViewModel.remainingTime.collectAsState()

    Box(
        modifier = Modifier
            .height(80.dp)
            .width(100.dp)
            .sectionShadow()
            .clickableWithRipple {
                onNavigate(Destination.Timer)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            HeadlineText(
                text =  DateConverter().formatTimeToMMSS(remainingTime),
                color = MaterialTheme.colorScheme.onBackground
            )
            FooterText(
                text = timer.name,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun NoTimerCard(
    height: Dp = 80.dp,
    onNavigate: (Destination) -> Unit
) {
    Box(
        modifier = Modifier
            .height(height)
            .wrapContentWidth()
            .sectionShadow()
            .clickableWithRipple {
                onNavigate(Destination.AddTimer)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            HeadlineText(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                text = "Noch keinen Timer",
                size = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            FooterText(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                text = "trage dein ersten Timer ein",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}