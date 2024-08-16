package de.frederikkohler.bauch_glueck.data.repository

import de.frederikkohler.bauch_glueck.data.local.database.entitiy.CountdownTimer

interface CountdownTimerRepository {
    suspend fun getTimers(refresh: Boolean): List<CountdownTimer>
    suspend fun updateTimer(timer: CountdownTimer)
    suspend fun deleteTimer(timer: CountdownTimer)
}

