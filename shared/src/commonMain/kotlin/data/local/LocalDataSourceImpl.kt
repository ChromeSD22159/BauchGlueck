package data.local

import data.local.entitiy.CountdownTimer

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