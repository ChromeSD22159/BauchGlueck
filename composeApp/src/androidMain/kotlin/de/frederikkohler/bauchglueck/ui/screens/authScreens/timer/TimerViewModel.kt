package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Repository
import data.local.LocalDatabase
import data.local.entitiy.CountdownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel(serverHost: String, db: LocalDatabase, deviceID: String): ViewModel() {
    private val repository: Repository = Repository(
        serverHost = serverHost,
        db = db,
        deviceID = deviceID
    )

    private var _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value.timer = repository.localDataSource.getAllTimer()
        }
    }

    data class TimerUiState(var timer: List<CountdownTimer> = emptyList())
}

