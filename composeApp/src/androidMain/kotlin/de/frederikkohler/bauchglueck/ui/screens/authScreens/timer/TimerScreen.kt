package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import data.local.entitiy.CountdownTimer
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.components.RoundImageButton
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import kotlinx.coroutines.flow.distinctUntilChanged
import navigation.Screens
import org.koin.androidx.compose.koinViewModel
import viewModel.TimerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerScreen(
    navController: NavController,
    viewModel: TimerViewModel,
    onEdit: (CountdownTimer) -> Unit = {},
) {

    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.uiState.value.timer.size }
            .distinctUntilChanged() // Nur bei tatsächlichen Änderungen auslösen
            .collect { size ->
                Log.d("TimerScreen.timer.size", "size changed to: $size")
            }
    }

    BackScaffold(
        title = Destination.Timer.title,
        navController = navController,
        topNavigationButtons = {
            Row {
                RoundImageButton(
                    icon = R.drawable.icon_sync,
                    modifier = Modifier.padding(end = 16.dp),
                    action = {
                        navController.navigate(Destination.AddTimer.route)
                    }
                )
            }

            Row {
                RoundImageButton(
                    icon = R.drawable.icon_gear,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        view = {
            viewModel.uiState.value.timer.forEach { timer ->
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
                        // viewModel.updateTimer(it)
                        // TODO("Save Timer updaten only local")
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    )
}