package data.local

import data.local.entitiy.CountdownTimer

interface LocalDataSource {

    suspend fun updateTimer(timer: CountdownTimer)

    suspend fun getAllTimer(): List<CountdownTimer>

    suspend fun deleteTimer(timer: CountdownTimer)

    suspend fun getTimers(refresh: Boolean): List<CountdownTimer>

    suspend fun getTimerByTimerId(timerId: String): CountdownTimer?

    suspend fun getEntriesSinceLastUpdate(lastUpdate: Long, userId: String): List<CountdownTimer>

    suspend fun insertTimer(timer: CountdownTimer)

}