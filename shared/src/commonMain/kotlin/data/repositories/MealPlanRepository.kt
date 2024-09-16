package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.MealDao
import data.remote.syncManager.MealPlanSyncManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth

class MealPlanRepository(
    db: LocalDatabase,
    var serverHost: String,
    var deviceID: String
): BaseRepository() {
    private var localService: MealDao = LocalDataSource(db).meals
    private var syncManager: MealPlanSyncManager = MealPlanSyncManager(db, serverHost, deviceID)

    suspend fun syncMealPlan() {
        syncManager.syncMealPlan()
    }
}