package data.local

import data.local.entitiy.CountdownTimer
import data.local.entitiy.SyncHistory

interface LocalDataSource {

    /*
    COUNTDOWN TIMER
     */
    suspend fun getAllTimer(): List<CountdownTimer>

    suspend fun getEntriesSinceLastUpdate(lastSync: Long): List<CountdownTimer>

    suspend fun updateTimer(timer: CountdownTimer)

    suspend fun updateTimers(timers: List<CountdownTimer>)

    suspend fun deleteTimer(timer: CountdownTimer)

    suspend fun getTimerByTimerId(timerId: String): CountdownTimer?

    suspend fun insertTimer(timer: CountdownTimer)

    /*
    SYNC HISTORY
     */
    suspend fun getLastSyncEntry(deviceID: String): SyncHistory?

    suspend fun insertSyncHistory(syncHistory: SyncHistory)

    suspend fun deleteAllSyncHistory()

}