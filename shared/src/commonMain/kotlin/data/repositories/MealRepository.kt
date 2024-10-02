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
    var deviceID: String
): BaseRepository() {
    private var localService: MealDao = LocalDataSource(db).meals
    private var syncManager: MealSyncManager = MealSyncManager(db, serverHost, deviceID)

    fun getAllMealsMeals(): Flow<List<MealWithCategories>> = localService.getMealWithCategories()

    suspend fun getMealWithCategoryById(mealId: String): MealWithCategories? = localService.getMealWithCategoryById(mealId)

    suspend fun syncLocalStartUpMeals() {
        syncManager.syncStartUpMeals()
    }
}