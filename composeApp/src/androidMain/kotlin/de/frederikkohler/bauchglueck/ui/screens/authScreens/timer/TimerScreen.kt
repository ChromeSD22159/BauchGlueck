package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import data.local.entitiy.CountdownTimer
import data.local.getDatabase
import data.network.ServerHost
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import kotlinx.datetime.Clock
import navigation.Screens
import util.KeyValueStorage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: TimerViewModel = viewModel {
        TimerViewModel(
            serverHost = ServerHost.LOCAL_SABINA.url,
            db = getDatabase(context = context.applicationContext),
            deviceID = KeyValueStorage(context.applicationContext).getOrCreateDeviceId()
        )
    }

    val uiState by viewModel.uiState.collectAsState()

    BackScaffold(
        title = Screens.Timer.title,
        navController = navController
    ) {

        uiState.timer.forEach { timer ->
            TimerCard(timer)
        }

    }
}