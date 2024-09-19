package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.WaterIntakeDao
import data.local.entitiy.WaterIntake
import data.remote.syncManager.WaterIntakeSyncManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atStartOfDayIn
import util.DateRepository
import kotlinx.datetime.TimeZone

class WaterIntakeRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
): BaseRepository() {

    private var localService: WaterIntakeDao = LocalDataSource(db).waterIntake
    private var syncManager: WaterIntakeSyncManager = WaterIntakeSyncManager(db, serverHost, deviceID)

    suspend fun getAll(): List<WaterIntake> {
        val currentUserId = userId ?: return emptyList()
        return localService.getAll(currentUserId)
    }

    fun getAllIntakesFromToday(): Flow<List<WaterIntake>> {
        if(userId == null) return emptyFlow()
        val today = DateRepository.startEndToday()
        return localService.getAllByDateRange(userId!!, today.start,today.end)
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
        val currentUserId = userId ?: return
        localService.hardDeleteAllByUserId(currentUserId)
    }

    suspend fun getAllAfterTimeStamp(timeStamp: Long): List<WaterIntake> {
        val currentUserId = userId ?: return emptyList()
        return localService.getAllAfterTimeStamp(
            timeStamp,
            currentUserId
        )
    }

    suspend fun syncWaterIntakes() {
        syncManager.syncWaterIntakes()
    }
}