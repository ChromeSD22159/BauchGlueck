package de.frederikkohler.bauch_glueck.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import de.frederikkohler.bauch_glueck.data.local.database.LocalDatabase
import de.frederikkohler.bauch_glueck.data.local.database.entitiy.CountdownTimer
import de.frederikkohler.bauch_glueck.data.repository.CountdownTimerRepository
import de.frederikkohler.bauch_glueck.data.repository.CountdownTimerRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomeScreenViewModel(
    db: LocalDatabase
) : ViewModel() {
    private val repository: CountdownTimerRepository = CountdownTimerRepositoryImpl(db)
    private val job = SupervisorJob()
    private val coroutineContext: CoroutineContext = job + Dispatchers.IO
    private val viewModelScope = CoroutineScope(coroutineContext)

    private val _timerList = MutableStateFlow<List<CountdownTimer>>(emptyList())
    val timerList: MutableStateFlow<List<CountdownTimer>>
        get() = _timerList


    init {
        getTimer()
    }

    private fun getTimer() {
        viewModelScope.launch {
            timerList.value = repository.getTimers(true)
        }
    }
}