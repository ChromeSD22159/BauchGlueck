package data.remote.syncManager

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.RoomTable
import data.local.dao.MealPlanDao
import data.local.dao.MedicationDao
import data.local.dao.SyncHistoryDao
import data.local.entitiy.MealPlanDay
import data.local.entitiy.MealPlanDayWithSpots
import data.remote.BaseApiClient

import data.remote.StrapiApiClient
import data.remote.model.ApiMealPlanDayResponse
import data.remote.model.SyncResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import util.debugJsonHelper
import util.onSuccess

class MealPlanSyncManager(
    db: LocalDatabase,
    serverHost: String,
    private var deviceID: String,
    private val table: RoomTable = RoomTable.MEAL_PLAN,
    private var user: FirebaseUser? = Firebase.auth.currentUser
): BaseSyncManager() {
    private val apiService: StrapiApiClient = StrapiApiClient()
    private var localService: MealPlanDao = LocalDataSource(db).mealPlan
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory

    suspend fun syncMealPlan() {
        if (user == null) return
        val lastSync =  syncHistory.lastSync(deviceID, table)
        val localChangedMealPlans = localService.getMealPlansWithSpotsAfterTimeStamp(lastSync)

        // Send Changed MealPlans to Server
        /*
        apiService.sendChangedEntriesToServer<MealPlanDayWithSpots, SyncResponse>(
            localChangedMealPlans,
            table,
            BaseApiClient.UpdateRemoteEndpoint.MEAL_PLAN
        )
        */


        // Receive Changed MealPlans from Server
        val response = apiService.fetchItemsAfterTimestamp<List<ApiMealPlanDayResponse>>(
            BaseApiClient.FetchAfterTimestampEndpoint.MealPlan,
            lastSync,
            user!!.uid
        )
        response.onSuccess { mealPlayDays ->
            mealPlayDays.forEach {

                val plan = it.toMealPlanDay()

                val slots: List<data.local.entitiy.MealPlanSpot> = emptyList()

                it.mealPlanSlots.forEach { slot ->
                    val mealPlanSlot = slot.toRoomMealPlanSlot()
                    mealPlanSlot.mealPlanDayId = plan.mealPlanDayId
                    mealPlanSlot.meal = Json.encodeToString(slot.meal)
                    mealPlanSlot.mealObject = slot.meal.toRoomMeal()
                    slots.plus(mealPlanSlot)
                }

                localService.insertMealPlanDay(plan)

                if (slots.isNotEmpty()) {
                    localService.insertMealPlanSpots(slots)
                }
            }
        }
    }
}