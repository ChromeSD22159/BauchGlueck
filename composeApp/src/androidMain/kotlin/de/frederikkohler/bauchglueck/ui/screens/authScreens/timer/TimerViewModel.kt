package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Repository
import data.local.LocalDatabase
import data.local.entitiy.CountdownTimer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import util.onError
import util.onSuccess

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

    fun syncLocalTimer() {
        viewModelScope.launch {
            _uiState.value.isLoading = true
            repository.syncLocalTimer().onSuccess {
                Log.i("TimerViewModel", "Sync Successfully $it")
            }.onError {
                Log.i("TimerViewModel", "Sync fails $it")
            }
            delay(500)
            _uiState.value.timer = repository.localDataSource.getAllTimer()
            _uiState.value.isLoading = false
        }
    }

    data class TimerUiState(
        var isLoading: Boolean = false,
        var timer: List<CountdownTimer> = emptyList()
    )
}

