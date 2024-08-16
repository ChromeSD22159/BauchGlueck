package de.frederikkohler.bauch_glueck

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frederikkohler.bauch_glueck.data.local.database.LocalDatabase
import de.frederikkohler.bauch_glueck.data.repository.CountdownTimerRepositoryImpl
import de.frederikkohler.bauch_glueck.ui.screens.home.HomeScreenViewModel

@Composable
fun App(db: LocalDatabase) {
    MaterialTheme {
        val timerViewModel = HomeScreenViewModel(db)

        Text("Hello World")
    }
}
