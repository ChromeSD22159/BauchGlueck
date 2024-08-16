package de.frederikkohler.bauch_glueck.data.local.database

import de.frederikkohler.bauch_glueck.data.local.database.entitiy.CountdownTimer

interface LocalDataSource {

    suspend fun updateTimer(timer: CountdownTimer)

    suspend fun getAllTimer(): List<CountdownTimer>

    suspend fun deleteTimer(timer: CountdownTimer)

}