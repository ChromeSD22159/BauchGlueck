package ui.screens.authScreens.timer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_timer
import bauchglueck.composeapp.generated.resources.ic_gear
import ui.navigations.Destination
import org.lighthousegames.logging.logging
import ui.components.theme.button.IconButton
import ui.components.theme.ScreenHolder
import viewModel.TimerScreenViewModel


@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.timerComposable(navController: NavHostController) {
    composable(Destination.Timer.route) {
        TimerScreen(
            navController = navController,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerScreen(
    navController: NavController,
) {
    val viewModel = viewModel<TimerScreenViewModel>()
    val timers by viewModel.allTimers.collectAsStateWithLifecycle(initialValue = emptyList())

    ScreenHolder(
        title = Destination.Timer.title,
        showBackButton = true,
        onNavigate = {
            navController.navigate(Destination.Home.route)
        },
        optionsRow = {
            IconButton(
                resource = Res.drawable.ic_add_timer,
                tint = MaterialTheme.colorScheme.onPrimary
            ) {
                navController.navigate(Destination.AddTimer.route)
            }

            IconButton(
                resource = Res.drawable.ic_gear,
                tint = MaterialTheme.colorScheme.onPrimary
            ) {
                navController.navigate(Destination.Settings.route)
            }
        },
        pageSpacing = 0.dp
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