package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import data.local.entitiy.Meal
import data.local.entitiy.MealCategory
import data.local.entitiy.MealPlanDay
import data.local.entitiy.MealPlanDayWithSpots
import data.local.entitiy.MealPlanSpot
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlanDay(mealPlanDay: MealPlanDay)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlanSpots(mealPlanSpots: List<MealPlanSpot>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlanSpot(mealPlanSpot: MealPlanSpot)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal)

    @Transaction
    suspend fun insertMealPlanDayAndSpot(day: MealPlanDay, spot: MealPlanSpot) {
        insertMealPlanDay(day)
        insertMealPlanSpot(spot)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(mealCategory: MealCategory)



    @Transaction
    @Query("SELECT * FROM mealPlanDay WHERE date = :date")
    suspend fun getMealPlanDayWithSpotsForDate(date: String): MealPlanDayWithSpots?

    @Transaction
    @Query("SELECT * FROM mealPlanDay WHERE date >= :startDate AND date <= :endDate")
    fun getMealPlanDaysWithSpotsForDateRangeAsFlow(startDate: String, endDate: String): Flow<List<MealPlanDayWithSpots>>

    @Transaction
    @Query("SELECT * FROM mealPlanDay WHERE updatedAtOnDevice > :timeStamp")
    suspend fun getMealPlansWithSpotsAfterTimeStamp(timeStamp: Long): List<MealPlanDayWithSpots>

    @Query("SELECT * FROM meals WHERE mealId = :mealId")
    suspend fun getMealById(mealId: String): Meal

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: String): MealCategory?
}