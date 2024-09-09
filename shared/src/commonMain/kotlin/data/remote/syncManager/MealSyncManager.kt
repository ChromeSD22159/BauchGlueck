package data.remote.syncManager

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.RoomTable
import data.local.dao.MealDao
import data.local.dao.SyncHistoryDao
import data.local.entitiy.MealCategoryCrossRef
import data.local.entitiy.MealWithCategories
import data.remote.StrapiMealApiClient
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import util.onSuccess

class MealSyncManager(
    db: LocalDatabase,
    serverHost: String,
    private var deviceID: String,
    private val table: RoomTable = RoomTable.Meal,
    private var user: FirebaseUser? = Firebase.auth.currentUser
) {
    private val apiService: StrapiMealApiClient = StrapiMealApiClient(serverHost)
    private var localService: MealDao = LocalDataSource(db).meals
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory

    private suspend fun sendChangedEntriesToServer(items: List<MealWithCategories>) {
        withContext(Dispatchers.IO) {
            try {
                logging().info { "Send Medication to Update on Server > > >" }
                items.forEach {
                    logging().info { "> > > Medication: $it" }
                }

                //val response = apiService.updateRemoteData(items)

                //logging().info { "Send Medication to Update on Server < < < $response" }

                // TODO: Handle Response - - Maybe Hard delete Medication? and send ids to server?
                //response.onSuccess { logging().info { "Medication Sync Success" } }
                //response.onError { logging().info { "Medication Sync Error" } }
            } catch (e: Exception) {
                logging().info { "Medication Sync Error" }
            }
        }
    }

    suspend fun syncStartUpMeals() {
        if (user == null) return

        val lastSync = syncHistory.getLatestSyncTimer(deviceID).sortedByDescending { it.lastSync }.firstOrNull { it.table == table }?.lastSync ?: 0L
        val localChangedMeals = localService.getMealsWithCategoriesAfterTimeStamp(lastSync)

        val startUpMealCountResponse = apiService.fetchStartUpMealsCount()
        startUpMealCountResponse.onSuccess {
            logging().info { "StartUpMealCount: ${it.length}" }

            var savedMealCount = 0
            var updatedMealCount = 0
            var savedCategoryCount = 0


            if(it.length != localChangedMeals.size) {
                apiService.fetchStartUpMeals(lastSync, user!!.uid)
                    .onSuccess {responseMealList ->
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
            } else {
                logging().info { "No New Meals to Sync" }
            }
        }
    }
}