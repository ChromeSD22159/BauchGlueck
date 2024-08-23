package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.MedicationDao
import data.local.dao.SyncHistoryDao
import data.local.dao.WaterIntakeDao
import data.local.entitiy.Medication
import data.local.entitiy.WaterIntake
import data.remote.StrapiCountdownTimerApiClient
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock

class WaterIntakeRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
) {

    private var localService: WaterIntakeDao = LocalDataSource(db).waterIntake
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory
    private val apiService: StrapiCountdownTimerApiClient = StrapiCountdownTimerApiClient(serverHost)

    suspend fun getAll(): List<WaterIntake> {
        return user?.let {
            localService.getAll(it.uid)

        } ?: emptyList()
    }

    suspend fun getById(timerId: String): WaterIntake? = this.localService.getById(timerId)

    suspend fun insertOrUpdate(countdownTimer: WaterIntake) = this.localService.insertOrUpdate(countdownTimer)

    suspend fun insertOrUpdate(countdownTimers: List<WaterIntake>) {
        countdownTimers.forEach {
            this.localService.insertOrUpdate(it.copy(updatedAt = Clock.System.now().toEpochMilliseconds()))
        }
    }

    suspend fun updateMany(countdownTimers: List<WaterIntake>) {
        val toUpdate = countdownTimers.map { it.copy(updatedAt = Clock.System.now().toEpochMilliseconds()) }
        localService.updateMany(toUpdate)
    }

    suspend fun softDeleteMany(countdownTimers: List<WaterIntake>) {
        val toUpdate = countdownTimers.map { it.copy(isDeleted = true) }
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

    suspend fun getAllAfterTimeStamp(timeStamp: Long): List<WaterIntake> {
        return user?.let {
            localService.getAllAfterTimeStamp(
                timeStamp,
                it.uid
            )
        } ?: emptyList()
    }

    /*
    suspend fun syncDataWithServer() {
        val lastSync = syncHistory.getLatestSyncTimer(deviceID)?.lastSync ?: 0L
        val pendingChanges = medications.getAllAfterTimeStamp(lastSync, user!!.uid)
        if (pendingChanges.isNotEmpty()) {

            val serverResponse = apiService.syncCountdownTimers(pendingChanges)

            serverResponse.onSuccess { timerResponse ->
                val deletedTimers :List<Medication?> = timerResponse.deletedTimers.map { medications.getById(it.timerId) }

                deletedTimers.forEach { timer : Medication? ->
                    timer?.let {
                        countdownTimer.hardDeleteOne(it)
                    }
                }
            }
        }
    }
     */
}