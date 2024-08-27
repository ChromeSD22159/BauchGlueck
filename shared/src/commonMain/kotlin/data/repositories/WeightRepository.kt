package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.WeightDao
import data.local.entitiy.Weight
import data.network.syncManager.WeightSyncManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock

class WeightRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
) {

    private var localService: WeightDao = LocalDataSource(db).weight
    private var syncManager: WeightSyncManager = WeightSyncManager(db, serverHost, deviceID)

    suspend fun getAll(): List<Weight> {
        return user?.let { firebaseUser ->
            localService.getAll(firebaseUser.uid).filter { !it.isDeleted }
        } ?: emptyList()
    }

    suspend fun getById(weightId: String): Weight? = this.localService.getById(weightId)

    suspend fun insertOrUpdate(weight: Weight) = this.localService.insertOrUpdate(weight)

    suspend fun insertOrUpdate(weights: List<Weight>) {
        weights.forEach {
            this.localService.insertOrUpdate(it.copy(updatedAt = Clock.System.now().toEpochMilliseconds().toString()))
        }
    }

    suspend fun updateMany(weights: List<Weight>) {
        val toUpdate = weights.map { it.copy(updatedAt = Clock.System.now().toEpochMilliseconds().toString()) }
        localService.updateMany(toUpdate)
    }

    suspend fun softDeleteMany(weights: List<Weight>) {
        val toUpdate = weights.map { it.copy(isDeleted = true) }
        localService.softDeleteMany(toUpdate)
    }

    suspend fun softDeleteById(weightId: String) {
        localService.softDeleteById(weightId)
    }

    suspend fun hardDeleteAllByUserId() {
        user?.let {
            localService.hardDeleteAllByUserId(it.uid)
        }
    }

    suspend fun getAllAfterTimeStamp(timeStamp: Long): List<Weight> {
        return user?.let {
            localService.getAllAfterTimeStamp(
                timeStamp,
                it.uid
            )
        } ?: emptyList()
    }

    suspend fun syncDataWithRemote() {
        syncManager.syncWeights()
    }
}