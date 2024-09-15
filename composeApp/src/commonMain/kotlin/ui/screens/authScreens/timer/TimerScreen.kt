package ui.screens.authScreens.timer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_timer
import bauchglueck.composeapp.generated.resources.ic_gear
import ui.components.BackScaffold
import ui.components.RoundImageButton
import ui.navigations.Destination
import org.lighthousegames.logging.logging
import viewModel.TimerScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerScreen(
    navController: NavController,
    backNavigationDirection: Destination = Destination.Home,
) {
    val viewModel = viewModel<TimerScreenViewModel>()
    val timers by viewModel.allTimers.collectAsStateWithLifecycle(initialValue = emptyList())

    BackScaffold(
        title = Destination.Timer.title,
        navController = navController,
        backNavigationDirection = backNavigationDirection,
        topNavigationButtons = {
            Row {
                RoundImageButton(
                    icon = Res.drawable.ic_add_timer,
                    modifier = Modifier.padding(end = 16.dp),
                    action = {
                        navController.navigate(Destination.AddTimer.route)
                    }
                )
            }

            Row {
                RoundImageButton(
                    icon = Res.drawable.ic_gear,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {
        timers.forEach { timer ->
            TimerCard(
                timer = timer,
                onClickEdit = {
                    navController.navigate(Destination.EditTimer.route)
                    navController.currentBackStackEntry?.savedStateHandle?.set("timerId", timer.timerId)
                },
                onDelete = {
                    Log.i("TimerCard Update", "isDeleted: ${it.isDeleted} - ${it.name}\n")
                    viewModel.softDelete(it)
                    Log.i("TimerCard Update", "isDeleted: ${it.isDeleted} - ${it.name}\n")
                }
            ) {
                logging().debug { "TIMER update ${it.name}" }
                logging().debug { it }

                viewModel.updateItemAndSyncRemote(it)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}