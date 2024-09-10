package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.WaterIntakeDao
import data.local.entitiy.WaterIntake
import data.remote.syncManager.WaterIntakeSyncManager
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
    private var syncManager: WaterIntakeSyncManager = WaterIntakeSyncManager(db, serverHost, deviceID)

    suspend fun getAll(): List<WaterIntake> {
        return user?.let {
            localService.getAll(it.uid)

        } ?: emptyList()
    }

    suspend fun getById(timerId: String): WaterIntake? = this.localService.getById(timerId)

    suspend fun insertOrUpdate(countdownTimer: WaterIntake) = this.localService.insertOrUpdate(countdownTimer)

    suspend fun insertOrUpdate(countdownTimers: List<WaterIntake>) {
        countdownTimers.forEach {
            this.localService.insertOrUpdate(it.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))
        }
    }

    suspend fun updateMany(countdownTimers: List<WaterIntake>) {
        val toUpdate = countdownTimers.map { it.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()) }
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

    suspend fun syncWaterIntakes() {
        syncManager.syncWaterIntakes()
    }
}