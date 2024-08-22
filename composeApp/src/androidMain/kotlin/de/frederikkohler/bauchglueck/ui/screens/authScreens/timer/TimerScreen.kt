package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.koinViewModel
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.components.RoundImageButton
import navigation.Screens
import viewModel.TimerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerScreen(
    navController: NavController
) {
    val viewModel = koinViewModel<TimerViewModel>()

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
                        viewModel.syncDataWithServer()
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
                TimerCard(timer, viewModel)
            }
        }
    )
}