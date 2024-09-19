package data.remote.syncManager

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.RoomTable
import data.local.dao.CountdownTimerDao
import data.local.dao.SyncHistoryDao
import data.local.entitiy.CountdownTimer
import data.remote.BaseApiClient
import data.remote.StrapiApiClient
import data.remote.model.SyncResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import org.lighthousegames.logging.logging
import util.onError
import util.onSuccess

class CountdownTimerSyncManager(
    db: LocalDatabase,
    serverHost: String,
    private var deviceID: String,
    private val table: RoomTable = RoomTable.COUNTDOWN_TIMER
): BaseSyncManager() {
    var user: FirebaseUser? = Firebase.auth.currentUser
    private val apiService: StrapiApiClient = StrapiApiClient()
    private var localService: CountdownTimerDao = LocalDataSource(db).countdownTimer
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory

    suspend fun syncTimers() {
        if (user == null) return

        val lastSync = syncHistory.lastSync(deviceID, table)
        val localChangedTimers = localService.getAllAfterTimeStamp(lastSync, user!!.uid)

        apiService.sendChangedEntriesToServer<CountdownTimer, SyncResponse>(
            localChangedTimers,
            table,
            BaseApiClient.UpdateRemoteEndpoint.COUNTDOWN_TIMER
        )

        logging().info { "* * * * * * * * * * SYNCING * * * * * * * * * * " }
        logging().info { "Last Sync Success: $lastSync" }

        // 3. Vom Server alle seit dem letzten Sync geänderten Timer abrufen
        val response = apiService.fetchItemsAfterTimestamp<List<CountdownTimer>>(
            BaseApiClient.FetchAfterTimestampEndpoint.COUNTDOWN_TIMER,
            lastSync,
            user!!.uid
        )

        response.onSuccess { serverTimers ->

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

            val newWeightStamp = syncHistory.setNewTimeStamp(table, deviceID)
            logging().info { "Save TimerStamp: ${newWeightStamp.lastSync}" }
        }
        response.onError { return }
    }
}