package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.RoomTable
import data.local.dao.CountdownTimerDao
import data.local.dao.SyncHistoryDao
import data.local.entitiy.CountdownTimer
import data.local.entitiy.SyncHistory
import data.remote.StrapiCountdownTimerApiClient
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging
import util.onError
import util.onSuccess

class CountdownTimerRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
) {
    private var localService: CountdownTimerDao = LocalDataSource(db).countdownTimer
    private var syncManager: SyncManager = SyncManager(db, serverHost, deviceID)

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

class SyncManager(
    db: LocalDatabase,
    serverHost: String,
    private var deviceID: String
) {
    private var user: FirebaseUser? = Firebase.auth.currentUser
    private val apiService: StrapiCountdownTimerApiClient = StrapiCountdownTimerApiClient(serverHost)
    private var localService: CountdownTimerDao = LocalDataSource(db).countdownTimer
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory

    private suspend fun sendChangedTimersToServer(timers: List<CountdownTimer>) {
        withContext(Dispatchers.IO) {
            try {
                logging().info { "Send Timer to Update on Server > > >" }
                timers.forEach {
                    logging().info { "> > > Timer: ${it.toString()}" }
                }
                val response = apiService.updateRemoteData(timers)

                // TODO: Handle Response - - Maybe Hard delete Timer? and send ids to server?
                response.onSuccess { logging().info { "Timer Sync Success" } }
                response.onError { logging().info { "Timer Sync Error" } }
            } catch (e: Exception) {
                logging().info { "Timer Sync Error" }
            }
        }
    }

    suspend fun syncTimers() {
        if (user == null) return

        val lastSync = syncHistory.getLatestSyncTimer(deviceID)?.sortedByDescending { it.lastSync }?.firstOrNull { it.table == RoomTable.COUNTDOWN_TIMER }?.lastSync ?: 0L
        val localChangedTimers = localService.getAllAfterTimeStamp(lastSync, user!!.uid)

        logging().info { "* * * * * * * * * * SYNCING * * * * * * * * * * " }
        logging().info { "Last Sync Success: $lastSync" }

        sendChangedTimersToServer(localChangedTimers)

        // 3. Vom Server alle seit dem letzten Sync geänderten Timer abrufen
        val response = apiService.fetchTimersAfterTimestamp(lastSync, user!!.uid)
        response.onSuccess { serverTimers ->

            logging().info { "serverTimers: ${serverTimers.size}" }
            logging().info { "localTimers: ${localChangedTimers.size}" }

            logging().info { "Received Timer to Update from Server < < <" }
            serverTimers.forEach {
                logging().info { "< < < Timer: ${it.toString()}" }
            }

            for (serverTimer in serverTimers) {
                val localTimer = localService.getById(serverTimer.timerId)

                if (localTimer != null) {
                    logging().info { "Timer: ${serverTimer.name}: ${serverTimer.updatedAtOnDevice}" }
                    logging().info { "Timer: ${localTimer.name}: ${localTimer.updatedAtOnDevice}" }
                    if (serverTimer.updatedAtOnDevice > localTimer.updatedAtOnDevice) {

                        // Wenn der Timer auf dem Server neuer ist, aktualisiere den lokalen Timer
                        localTimer.name = serverTimer.name
                        localTimer.duration = serverTimer.duration
                        localTimer.timerState = serverTimer.timerState
                        localTimer.startDate = serverTimer.startDate
                        localTimer.endDate = serverTimer.endDate
                        localTimer.updatedAtOnDevice = serverTimer.updatedAtOnDevice
                        localTimer.isDeleted = serverTimer.isDeleted
                        localTimer.updatedAt = serverTimer.updatedAt

                        localService.insertOrUpdate(localTimer)
                    }
                } else {
                    // Wenn der Timer noch nicht lokal existiert, füge ihn hinzu
                    localService.insert(
                        CountdownTimer(
                            timerId = serverTimer.timerId,
                            userId = serverTimer.userId,
                            name = serverTimer.name,
                            duration = serverTimer.duration,
                            timerState = serverTimer.timerState,
                            startDate = serverTimer.startDate,
                            endDate = serverTimer.endDate,
                            updatedAtOnDevice = serverTimer.updatedAtOnDevice,
                            isDeleted = serverTimer.isDeleted,
                            updatedAt = serverTimer.updatedAt
                        )
                    )
                }
            }

            val newTimerStamp = SyncHistory(deviceId = deviceID,table = RoomTable.COUNTDOWN_TIMER)
            syncHistory.insertSyncHistory(newTimerStamp)

            logging().info { "Save TimerStamp: ${newTimerStamp.lastSync}" }
        }
        response.onError { return }
    }
}