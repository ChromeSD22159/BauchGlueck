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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.components.RoundImageButton
import navigation.Screens
import org.koin.androidx.compose.koinViewModel
import viewModel.TimerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerScreen(
    navController: NavController,
) {
    val viewModel: TimerViewModel = koinViewModel()
    viewModel.getAllCountdownTimers()
    val uiState by viewModel.uiState.collectAsState()

    BackScaffold(
        title = Screens.Timer.title,
        navController = navController,
        topNavigationButtons = {
            Row {
                RoundImageButton(
                    icon = R.drawable.icon_sync,
                    modifier = Modifier.padding(end = 16.dp),
                    action = {
                        navController.navigate(Screens.AddTimer.route)
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
            uiState.timer.forEach { timer ->
                TimerCard(
                    timer = timer,
                    onDelete = {
                        Log.i("TimerCard Update", "isDeleted: ${it.isDeleted} - ${it.name}\n")
                        viewModel.softDeleteTimer(it)
                        Log.i("TimerCard Update", "isDeleted: ${it.isDeleted} - ${it.name}\n")
                    },
                    onTimerUpdate = {
                        Log.i("TimerCard Update", "StartTimer: ${it.name} - ${it.timerState}\n")
                        viewModel.updateTimer(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    )
}