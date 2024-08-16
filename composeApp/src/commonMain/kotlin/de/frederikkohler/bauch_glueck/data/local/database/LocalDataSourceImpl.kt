package de.frederikkohler.bauch_glueck.data.local.database

import de.frederikkohler.bauch_glueck.data.local.database.entitiy.CountdownTimer

class LocalDataSourceImpl(
    private val db: LocalDatabase
): LocalDataSource {
    override suspend fun updateTimer(timer: CountdownTimer) {
        db.timerDao.updateTimer(timer)
    }

    override suspend fun getAllTimer(): List<CountdownTimer> {
        return db.timerDao.getAllTimer()
    }

    override suspend fun deleteTimer(timer: CountdownTimer) {
        db.timerDao.deleteTimerById(timer.timerId)
    }
}