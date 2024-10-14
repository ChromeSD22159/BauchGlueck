package data.remote.syncManager

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.RoomTable
import data.local.dao.MealDao
import data.local.dao.SyncHistoryDao
import data.local.entitiy.MealCategoryCrossRef
import data.remote.BaseApiClient
import data.remote.StrapiApiClient
import data.remote.model.ApiRecipesResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import org.lighthousegames.logging.logging
import util.onSuccess

class MealSyncManager(
    db: LocalDatabase,
    serverHost: String,
    private var deviceID: String,
    private val table: RoomTable = RoomTable.Meal,
    private var user: FirebaseUser? = Firebase.auth.currentUser
): BaseSyncManager() {
    private val apiService: StrapiApiClient = StrapiApiClient()
    private var localService: MealDao = LocalDataSource(db).meals
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory

    suspend fun syncStartUpMeals() {
        if (user == null) return

        val lastSync = syncHistory.lastSync(deviceID, table)

        var savedMealCount = 0
        var updatedMealCount = 0
        var savedCategoryCount = 0

        val response = apiService.fetchItemsAfterTimestamp<List<ApiRecipesResponse>>(
            BaseApiClient.FetchAfterTimestampEndpoint.Recipe,
            lastSync,
            user!!.uid
        )
        response.onSuccess {responseMealList ->
            responseMealList.forEach { responseMeal ->
                val cat = responseMeal.category.toRoomCategory()
                val meal = responseMeal.toRoomMeal()
                meal.categoryId = cat.categoryId

                val existingCategory = localService.getCategoryById(cat.categoryId)
                if (existingCategory == null) {
                    localService.insertCategory(cat)
                    savedCategoryCount++
                }

                val existingMeal = localService.getMealById(meal.mealId)
                if (existingMeal == null) {
                    localService.insertMeal(meal)
                    savedMealCount++
                } else if (existingMeal.updatedAtOnDevice!! < meal.updatedAtOnDevice!!) {
                    localService.updateMeal(meal)
                    updatedMealCount++
                }

                if(existingMeal != null && existingCategory != null) {
                    localService.insertMealCategoryCrossRef(MealCategoryCrossRef(meal.mealId, cat.categoryId))
                }

                logging().info { "* * * * * * * * * * SYNCING DONE * * * * * * * * * * " }
                logging().info { "* * * * Updated Meals: $updatedMealCount" }
                logging().info { "* * * * Saved Meals: $savedMealCount" }
                logging().info { "* * * * Saved Categories: $savedCategoryCount" }
            }
        }
    }
}