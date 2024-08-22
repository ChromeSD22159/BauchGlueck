package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.CountdownTimerDao
import data.local.dao.SyncHistoryDao
import data.local.entitiy.CountdownTimer
import data.remote.StrapiCountdownTimerApiClient
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock
import util.onSuccess

class CountdownTimerRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
) {
    private var countdownTimer: CountdownTimerDao = LocalDataSource(db).countdownTimer
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory
    private val apiService: StrapiCountdownTimerApiClient = StrapiCountdownTimerApiClient(serverHost)

    suspend fun getAllCountdownTimers(): List<CountdownTimer> {
        return user?.let {
            countdownTimer.getAllCountdownTimers(it.uid)
        } ?: emptyList()
    }

    suspend fun getCountdownTimerById(timerId: String): CountdownTimer? = this.countdownTimer.getCountdownTimerById(timerId)

    suspend fun insertOrUpdateCountdownTimer(countdownTimer: CountdownTimer) = this.countdownTimer.insertOrUpdateCountdownTimer(countdownTimer)

    suspend fun insertOrUpdateCountdownTimers(countdownTimers: List<CountdownTimer>) {
        countdownTimers.forEach {
            this.countdownTimer.insertOrUpdateCountdownTimer(it.copy(updatedAt = Clock.System.now().toEpochMilliseconds()))
        }
    }

    suspend fun updateCountdownTimers(countdownTimers: List<CountdownTimer>) {
        countdownTimer.updateCountdownTimers(countdownTimers)
    }

    suspend fun softDeleteCountdownTimers(ids: List<Int>) {
            countdownTimer.softDeleteCountdownTimer(ids)
    }

    suspend fun deleteAllCountdownTimers() {
        user?.let {
            countdownTimer.deleteAllCountdownTimers(it.uid)
        }
    }

    suspend fun getCountdownTimersAfterTimeStamp(timeStamp: Long): List<CountdownTimer> {
        return user?.let {
            countdownTimer.getCountdownTimersAfterTimeStamp(
                timeStamp,
                it.uid
            )
        } ?: emptyList()
    }

    suspend fun syncDataWithServer() {
        val lastSync = syncHistory.getLatestSyncTimer(deviceID)?.lastSync ?: 0L
        val pendingChanges = countdownTimer.getCountdownTimersAfterTimeStamp(lastSync, user!!.uid)
        if (pendingChanges.isNotEmpty()) {

            val serverResponse = apiService.syncCountdownTimers(pendingChanges)

            serverResponse.onSuccess { timerResponse ->
                val deletedTimers :List<CountdownTimer?> = timerResponse.deletedTimers.map { countdownTimer.getCountdownTimerById(it.timerId) }

                deletedTimers.forEach { timer : CountdownTimer? ->
                    timer?.let {
                        countdownTimer.deleteCountdownTimer(it)
                    }
                }
            }
        }
    }
}