package data.local

import data.local.entitiy.CountdownTimer

interface LocalDataSource {

    suspend fun updateTimer(timer: CountdownTimer)

    suspend fun getAllTimer(): List<CountdownTimer>

    suspend fun deleteTimer(timer: CountdownTimer)

}