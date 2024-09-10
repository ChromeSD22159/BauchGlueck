package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.MealDao
import data.local.entitiy.MealWithCategories
import data.remote.syncManager.MealSyncManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow

class MealRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
) {
    private var localService: MealDao = LocalDataSource(db).meals
    private var syncManager: MealSyncManager = MealSyncManager(db, serverHost, deviceID)

    fun getAllMealsMeals(): Flow<List<MealWithCategories>> = localService.getMealWithCategories()

    suspend fun syncLocalStartUpMeals() {
        syncManager.syncStartUpMeals()
    }
}