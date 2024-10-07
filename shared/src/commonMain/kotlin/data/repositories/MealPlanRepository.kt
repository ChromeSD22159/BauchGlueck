package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.MealPlanDao
import data.local.entitiy.MealPlanDay
import data.local.entitiy.MealPlanDayWithSpots
import data.local.entitiy.MealPlanSpot
import data.remote.syncManager.MealPlanSyncManager
import kotlinx.datetime.LocalDate

class MealPlanRepository(
    db: LocalDatabase,
    var serverHost: String,
    var deviceID: String
): BaseRepository() {
    private var localService: MealPlanDao = LocalDataSource(db).mealPlan
    private var syncManager: MealPlanSyncManager = MealPlanSyncManager(db, serverHost, deviceID)

    fun getMealPlanDaysWithSpotsForDateRangeAsFlow(startDate: LocalDate, endDate: LocalDate) = localService.getMealPlanDaysWithSpotsForDateRangeAsFlow(startDate.toString(), endDate.toString())

    suspend fun getMealPlanDayWithSpotsForDate(date: String): MealPlanDayWithSpots? = localService.getMealPlanDayWithSpotsForDate(date)

    suspend fun insertMealPlanDay(mealPlanDay: MealPlanDay) = localService.insertMealPlanDay(mealPlanDay)

    suspend fun insertMealPlanSpot(mealPlanSpots: MealPlanSpot) = localService.insertMealPlanSpot(mealPlanSpots)

    suspend fun insertMealPlanDayAndSpot(mealPlanSpots: MealPlanSpot, mealPlanDay: MealPlanDay) = localService.insertMealPlanDayAndSpot(mealPlanDay, mealPlanSpots)

    suspend fun syncMealPlan() = syncManager.syncMealPlan()
}