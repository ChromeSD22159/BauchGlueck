package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.CountdownTimerDao
import data.local.entitiy.CountdownTimer
import data.remote.syncManager.CountdownTimerSyncManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging

class CountdownTimerRepository(
    db: LocalDatabase,
    var serverHost: String,
    var deviceID: String
): BaseRepository() {
    private var localService: CountdownTimerDao = LocalDataSource(db).countdownTimer
    private var syncManager: CountdownTimerSyncManager = CountdownTimerSyncManager(db, serverHost, deviceID)

    fun getAll(): Flow<List<CountdownTimer>> {
        val currentUserId = userId ?: return emptyFlow()
        logging().info { "CountdownTimerRepository getAll $currentUserId" }
         val timer = localService.getAll(currentUserId).map { items ->
             items.filter { !it.isDeleted }
         }

        return timer
    }

    suspend fun getById(timerId: String): CountdownTimer? = this.localService.getById(timerId)

    suspend fun insertOrUpdate(countdownTimer: CountdownTimer) = this.localService.insertOrUpdate(countdownTimer.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))

    suspend fun softDeleteMany(countdownTimers: List<CountdownTimer>) {
        val toUpdate = countdownTimers.map { it.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()) }
            localService.softDeleteMany(toUpdate)
    }

    suspend fun getAllAfterTimeStamp(timeStamp: Long): List<CountdownTimer> {
        val currentUserId = userId ?: return emptyList()
        return localService.getAllAfterTimeStamp(
            timeStamp,
            currentUserId
        )
    }

    suspend fun syncDataWithRemote() {
        syncManager.syncTimers()
    }
}