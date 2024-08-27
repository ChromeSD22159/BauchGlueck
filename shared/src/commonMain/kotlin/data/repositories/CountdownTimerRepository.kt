package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.CountdownTimerDao
import data.local.entitiy.CountdownTimer
import data.network.syncManager.CountdownTimerSyncManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock

class CountdownTimerRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
) {
    private var localService: CountdownTimerDao = LocalDataSource(db).countdownTimer
    private var syncManager: CountdownTimerSyncManager = CountdownTimerSyncManager(db, serverHost, deviceID)

    suspend fun getAll(): List<CountdownTimer> {
        return user?.let { firebaseUser ->
            localService.getAll(firebaseUser.uid).filter { !it.isDeleted }
        } ?: emptyList()
    }

    suspend fun getById(timerId: String): CountdownTimer? = this.localService.getById(timerId)

    suspend fun insertOrUpdate(countdownTimer: CountdownTimer) = this.localService.insertOrUpdate(countdownTimer.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))

    suspend fun insertOrUpdate(countdownTimers: List<CountdownTimer>) {
        countdownTimers.forEach {
            this.localService.insertOrUpdate(it.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))
        }
    }

    suspend fun softDeleteMany(countdownTimers: List<CountdownTimer>) {
        val toUpdate = countdownTimers.map { it.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()) }
            localService.softDeleteMany(toUpdate)
    }

    suspend fun getAllAfterTimeStamp(timeStamp: Long): List<CountdownTimer> {
        return user?.let {
            localService.getAllAfterTimeStamp(
                timeStamp,
                it.uid
            )
        } ?: emptyList()
    }

    suspend fun syncDataWithRemote() {
        syncManager.syncTimers()
    }
}