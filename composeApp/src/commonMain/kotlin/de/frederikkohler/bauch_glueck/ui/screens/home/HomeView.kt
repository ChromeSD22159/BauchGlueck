package de.frederikkohler.bauch_glueck.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import de.frederikkohler.bauch_glueck.data.local.database.entitiy.CountdownTimer
import de.frederikkohler.bauch_glueck.data.repository.CountdownTimerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.coroutines.CoroutineContext

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel
) {
    val timer by viewModel.timerList.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Success ${viewModel.timerList.value.size}")
    }
}