package data.local

import data.local.entitiy.CountdownTimer
import data.local.entitiy.SyncHistory

class LocalDataSourceImpl(
    db: LocalDatabase
): LocalDataSource {

    private var countdownTimer = db.timerDao
    private var syncHistory = db.syncHistoryDao


    /*
    COUNTDOWN TIMER
     */
    override suspend fun updateTimer(timer: CountdownTimer) {
        countdownTimer.updateTimer(timer)
    }

    override suspend fun updateTimers(timers: List<CountdownTimer>) {
        countdownTimer.updateTimers(timers)
    }

    override suspend fun getAllTimer(): List<CountdownTimer> {
        return countdownTimer.getAllTimer()
    }

    override suspend fun deleteTimer(timer: CountdownTimer) {
        countdownTimer.deleteTimerById(timer.timerId)
    }

    override suspend fun getTimerByTimerId(timerId: String): CountdownTimer? {
        return countdownTimer.getTimerByTimerId(timerId)
    }

    override suspend fun insertTimer(timer: CountdownTimer) {
        countdownTimer.insertTimer(timer)
    }

    override suspend fun getEntriesSinceLastUpdate(lastSync: Long): List<CountdownTimer> {
        return countdownTimer.getEntriesSinceLastUpdate(lastSync)
    }

    /*
    SYNC HISTORY
     */
    override suspend fun getLastSyncEntry(deviceID: String): SyncHistory? {
        return syncHistory.getLatestSyncTimer(deviceID)
    }

    override suspend fun insertSyncHistory(syncHistory: SyncHistory) {
        this.syncHistory.insertSyncHistory(syncHistory)
    }

    override suspend fun deleteAllSyncHistory() {
        this.syncHistory.deleteAllSyncHistory()
    }
}