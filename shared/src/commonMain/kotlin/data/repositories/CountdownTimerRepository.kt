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
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging
import util.NetworkError
import util.Result
import util.onError
import util.onSuccess
import util.toIsoDate

class CountdownTimerRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
) {
    var localService: CountdownTimerDao = LocalDataSource(db).countdownTimer
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory
    private val apiService: StrapiCountdownTimerApiClient = StrapiCountdownTimerApiClient(serverHost)

    suspend fun getAll(): List<CountdownTimer> {
        return user?.let {
            localService.getAll(it.uid)

        } ?: emptyList()
    }

    suspend fun getById(timerId: String): CountdownTimer? = this.localService.getById(timerId)

    suspend fun insertOrUpdate(countdownTimer: CountdownTimer) = this.localService.insertOrUpdate(countdownTimer.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))

    suspend fun insertOrUpdate(countdownTimers: List<CountdownTimer>) {
        countdownTimers.forEach {
            this.localService.insertOrUpdate(it.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))
        }
    }

    suspend fun updateMany(countdownTimers: List<CountdownTimer>) {
        val toUpdate = countdownTimers.map { it.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()) }
        localService.updateMany(toUpdate)
    }

    suspend fun softDeleteMany(countdownTimers: List<CountdownTimer>) {
        val toUpdate = countdownTimers.map { it.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()) }
            localService.softDeleteMany(toUpdate)
    }

    suspend fun softDeleteById(timerId: String) {
        localService.softDeleteById(timerId)
    }

    suspend fun hardDeleteAllByUserId() {
        user?.let {
            localService.hardDeleteAllByUserId(it.uid)
        }
    }

    suspend fun getAllAfterTimeStamp(timeStamp: Long): List<CountdownTimer> {
        return user?.let {
            localService.getAllAfterTimeStamp(
                timeStamp,
                it.uid
            )
        } ?: emptyList()
    }

    suspend fun updateRemoteData() {
        val lastSync = syncHistory.getLatestSyncTimer(deviceID)?.firstOrNull { it.table == RoomTable.COUNTDOWN_TIMER }?.lastSync ?: 0L // 1724493065017
        val pendingChanges = localService.getAllAfterTimeStamp(lastSync, user!!.uid)
        if (pendingChanges.isNotEmpty()) {

            val serverResponse = apiService.updateRemoteData(pendingChanges)

            serverResponse.onSuccess { timerResponse ->
                val deletedTimers: List<CountdownTimer?> = timerResponse.deletedTimers.map { localService.getById(it.timerId) }

                deletedTimers.forEach { timer : CountdownTimer? ->
                    timer?.let {
                        localService.hardDeleteOne(it)
                    }
                }

                syncHistory.insertSyncHistory(
                    SyncHistory(
                        deviceId = deviceID,
                        table = RoomTable.COUNTDOWN_TIMER,
                        lastSync = Clock.System.now().toEpochMilliseconds()
                    )
                )
            }
        }
    }

    suspend fun updateLocalData(): Result<List<CountdownTimer>, NetworkError> {
        val lastSync = syncHistory.getLatestSyncTimer(deviceID)?.sortedByDescending { it.lastSync }?.firstOrNull { it.table == RoomTable.COUNTDOWN_TIMER }?.lastSync ?: 0L
        logging().info { "* * * * * * * * * * * * * * * * * * * * * * * * * * Start UpdateLocalData * * * * * * * * * * * * * * * * * * * * * * * * * *" }
        logging().info { "Last Sync TimeStamp: $lastSync" }
        logging().info { "Firebase User: ${user?.uid}" }

        val error: NetworkError? = null

        if(user != null) {
            if (lastSync == 0L) {
                // Initial Sync: Fetch all timers from the server
                apiService.getCountdownTimers(user!!.uid).onSuccess { timerList ->
                    timerList.map { localService.insertOrUpdate(it.toCountdownTimer()) }

                    if(timerList.isNotEmpty()) {
                        syncHistory.insertSyncHistory(
                            SyncHistory(
                                deviceId = deviceID,
                                table = RoomTable.COUNTDOWN_TIMER
                            )
                        )

                        logging().info { "Saved Sync TimeStamp: ${Clock.System. now().toEpochMilliseconds()}" }
                    } else {
                        logging().info { "No Data to Sync from Server" }
                    }

                    Result.Success(localService.getAll(user!!.uid))
                }.onError {
                    Result.Error(it)
                }
            } else {
                // Incremental Sync: Fetch only updated timers
                val localDataAfterTimeStampTemp = mutableListOf<CountdownTimer>() // 1
                localService.getAllAfterTimeStamp(lastSync, user!!.uid).toMutableList().forEach { localDataAfterTimeStampTemp.add(it) }

                apiService.fetchTimersAfterTimestamp(lastSync, user!!.uid).onSuccess { remoteTimerList ->
                    logging().info { "Received Data from Server: ${remoteTimerList.size}" }
                    remoteTimerList.map {
                        localService.insertOrUpdate(it)
                        localDataAfterTimeStampTemp.remove(it)
                    }

                    if(remoteTimerList.isNotEmpty()) {
                        logging().info { "insertOrUpdate Timers: ${remoteTimerList.size}" }
                    }

                    if(localDataAfterTimeStampTemp.isNotEmpty()) {
                        logging().info {"delete Timers: ${localDataAfterTimeStampTemp.size}" }
                    }

                    localDataAfterTimeStampTemp.map {
                        localService.hardDeleteOne(it)
                    }


                    if(remoteTimerList.isNotEmpty()) {
                        syncHistory.insertSyncHistory(
                            SyncHistory(
                                deviceId = deviceID,
                                table = RoomTable.COUNTDOWN_TIMER
                            )
                        )

                        logging().info { "Saved Sync TimeStamp: ${Clock.System. now().toEpochMilliseconds()}" }
                    }

                    Result.Success(localService.getAll(user!!.uid))
                }.onError {
                    logging().info { "No Data to Sync from Server" }
                }
            }
        } else {
            Result.Error(NetworkError.UNAUTHORIZED)
        }

        return if (error != null) {
            Result.Error(error)
        } else {
            val timer = localService.getAll(user!!.uid)
            logging().info { "Repository Data after update: ${timer.size}" }
            Result.Success(timer)
        }


    }
}
