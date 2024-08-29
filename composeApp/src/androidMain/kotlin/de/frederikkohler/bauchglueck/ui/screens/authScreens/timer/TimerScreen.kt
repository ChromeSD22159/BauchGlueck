package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import data.local.entitiy.CountdownTimer
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.components.RoundImageButton
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import viewModel.TimerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerScreen(
    navController: NavController,
    viewModel: TimerViewModel,
    backNavigationDirection: Destination = Destination.Home,
    onEdit: (CountdownTimer) -> Unit = {},
) {

    val timers by viewModel.uiState.value.timers.collectAsState()

    BackScaffold(
        title = Destination.Timer.title,
        navController = navController,
        backNavigationDirection = backNavigationDirection,
        topNavigationButtons = {
            Row {
                RoundImageButton(
                    icon = R.drawable.ic_add_timer,
                    modifier = Modifier.padding(end = 16.dp),
                    action = {
                        navController.navigate(Destination.AddTimer.route)
                    }
                )
            }

            Row {
                RoundImageButton(
                    icon = R.drawable.ic_gear,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        view = {
            timers.forEach { timer ->
                TimerCard(
                    timer = timer,
                    onEditSave = {
                        onEdit(timer)
                    },
                    onDelete = {
                        Log.i("TimerCard Update", "isDeleted: ${it.isDeleted} - ${it.name}\n")
                        viewModel.softDeleteTimer(it)
                        Log.i("TimerCard Update", "isDeleted: ${it.isDeleted} - ${it.name}\n")
                    },
                    onTimerUpdate = {
                        Log.i("TimerCard Update", "StartTimer: ${it.name} - ${it.timerState}\n")
                        // viewModel.updateTimer(it).
                        // TODO("Save Timer updaten only local")
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    )
}