package de.frederikkohler.bauch_glueck.data.repository

import de.frederikkohler.bauch_glueck.data.local.database.LocalDataSource
import de.frederikkohler.bauch_glueck.data.local.database.LocalDataSourceImpl
import de.frederikkohler.bauch_glueck.data.local.database.LocalDatabase
import de.frederikkohler.bauch_glueck.data.local.database.entitiy.CountdownTimer

class CountdownTimerRepositoryImpl(
    private val db: LocalDatabase
) : CountdownTimerRepository {
    private val localDataSource: LocalDataSource = LocalDataSourceImpl(db)
    override suspend fun getTimers(refresh: Boolean): List<CountdownTimer> {
        return localDataSource.getAllTimer()
    }

    override suspend fun updateTimer(timer: CountdownTimer) {
        localDataSource.updateTimer(timer)
    }

    override suspend fun deleteTimer(timer: CountdownTimer) {
        localDataSource.deleteTimer(timer)
    }

}