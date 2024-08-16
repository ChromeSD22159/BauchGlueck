package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import data.local.CountdownTimerRepository
import data.local.LocalDataSourceImpl
import data.local.entitiy.CountdownTimer
import data.local.getDatabase
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import kotlinx.coroutines.launch
import navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerScreen(navController: NavController) {
    BackScaffold(
        title = Screens.Timer.title,
        navController = navController
    ) {
        val context = LocalContext.current
        val countdownTimerRepository = LocalDataSourceImpl(getDatabase(context))
        val scope = rememberCoroutineScope()

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                scope.launch {
                    countdownTimerRepository.getAllTimer()
                    Toast.makeText(context, "Timer added", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(text = "Get Timer")
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                scope.launch {
                    var countdownTimer = CountdownTimer(
                        timerId = "",
                        userId = "",
                        deviceID = "",
                        name = "",
                        duration = 0,
                        timerState = "",
                        timerType = "",
                    )
                    countdownTimerRepository.insertTimer(countdownTimer)
                }
            }) {
            Text(text = "Add Timer")
        }


    }
}

