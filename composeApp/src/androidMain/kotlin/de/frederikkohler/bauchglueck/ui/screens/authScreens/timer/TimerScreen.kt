package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import data.local.entitiy.CountdownTimer
import data.local.getDatabase
import data.network.ServerHost
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.components.RoundImageButton
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
        navController = navController,
        topNavigationButtons = {
            Row {
                RoundImageButton(
                    icon = R.drawable.icon_sync,
                    modifier = Modifier.padding(end = 16.dp),
                    action = {
                        viewModel.syncLocalTimer()
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
                TimerCard(timer)
            }
        }
    )
}