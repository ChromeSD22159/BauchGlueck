package ui.screens.authScreens.timer

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
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
import kotlinx.datetime.Clock
import ui.components.theme.DropDownMenu
import ui.components.theme.DropdownMenuRow
import ui.components.theme.button.IconButton
import ui.components.theme.sectionShadow
import ui.components.theme.text.HeadlineText
import ui.theme.AppTheme
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

    val timerCardViewModelViewModel: TimerCardViewModel = viewModel(
        key = "TimerController_${timer.timerId}",
        factory = viewModelFactory {
            initializer {
                TimerCardViewModel(timer)
            }
        }
    )

    val remainingTime by timerCardViewModelViewModel.remainingTime.collectAsState()
    val timerState by timerCardViewModelViewModel.timerState.collectAsState()
    val startDate by timerCardViewModelViewModel.startDate.collectAsState()
    val endDate by timerCardViewModelViewModel.endDate.collectAsState()

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
            .sectionShadow()
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
               HeadlineText(
                   modifier = Modifier.padding(top = 5.dp),
                   text = timer.name,
                   color = MaterialTheme.colorScheme.onBackground,
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
                           IconButton(
                               resource = Res.drawable.icon_pause
                           ) { timerCardViewModelViewModel.pause() }

                           IconButton(
                               resource = Res.drawable.ic_stop
                           ) { timerCardViewModelViewModel.stop() }
                       }
                   }
                   TimerState.paused -> IconButton(resource = Res.drawable.ic_play) { timerCardViewModelViewModel.resume() }
                   TimerState.completed -> IconButton(resource = Res.drawable.icon_sync) { timerCardViewModelViewModel.reset() }
                   TimerState.notRunning -> IconButton(resource = Res.drawable.ic_play) { timerCardViewModelViewModel.start() }
               }

               HeadlineText(
                   text = remainingTime.formatTimeToMMSS(),
                   color = MaterialTheme.colorScheme.onBackground,
                   size = MaterialTheme.typography.headlineLarge.fontSize,
                   weight = FontWeight.SemiBold
               )
           }
       }
    }
}