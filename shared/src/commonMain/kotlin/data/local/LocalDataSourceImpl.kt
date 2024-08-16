package data.local

import data.local.entitiy.CountdownTimer

class LocalDataSourceImpl(
    private val db: LocalDatabase
): LocalDataSource {

    private var countdownTimer = db.timerDao

    override suspend fun updateTimer(timer: CountdownTimer) {
        countdownTimer.updateTimer(timer)
    }

    override suspend fun getAllTimer(): List<CountdownTimer> {
        return countdownTimer.getAllTimer()
    }

    override suspend fun deleteTimer(timer: CountdownTimer) {
        countdownTimer.deleteTimerById(timer.timerId)
    }

    override suspend fun getTimers(refresh: Boolean): List<CountdownTimer> {
        return countdownTimer.getAllTimer()
    }

    override suspend fun getTimerByTimerId(timerId: String): CountdownTimer? {
        return countdownTimer.getTimerByTimerId(timerId)
    }

    override suspend fun getEntriesSinceLastUpdate(
        lastUpdate: Long,
        userId: String
    ): List<CountdownTimer> {
        TODO("Not yet implemented")
    }

    override suspend fun insertTimer(timer: CountdownTimer) {
        countdownTimer.insertTimer(timer)
    }
}