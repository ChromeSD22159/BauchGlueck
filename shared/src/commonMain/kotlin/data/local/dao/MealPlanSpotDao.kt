package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import data.local.entitiy.MealPlanDayWithSpots
import data.local.entitiy.MealPlanSpot
import data.local.entitiy.MealPlanSpotWithMeal
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanSpotDao {

    // Fügt einen neuen MealPlanSpot ein oder ersetzt einen vorhandenen
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMealPlanSpotWithMeal(mealPlanSpotWithMeal: MealPlanSpotWithMeal)

    // Abfrage eines MealPlanSpots anhand von mealPlanDayId und mealId
    @Query("SELECT * FROM mealPlanSpot WHERE mealPlanDayId = :mealPlanDayId AND mealId = :mealId")
    suspend fun getMealPlanSpot(mealPlanDayId: String, mealId: String): MealPlanSpot?

    // Holt MealPlanDay mit allen MealPlanSpots für ein bestimmtes Datum
    @Transaction
    @Query("SELECT * FROM mealPlanDay WHERE date = :date")
    fun getMealPlanDayWithSpotsByDate(date: String): Flow<MealPlanDayWithSpots?>

    // Holt MealPlanDay mit allen MealPlanSpots innerhalb eines Datumsbereichs
    @Transaction
    @Query("SELECT * FROM mealPlanDay WHERE date BETWEEN :startDate AND :endDate")
    fun getMealPlanDaysWithSpotsInRange(startDate: String, endDate: String): Flow<List<MealPlanDayWithSpots>>

    // Entfernt die Verbindung zwischen einem Meal und MealPlanSpot, behält jedoch den Spot
    @Query("UPDATE mealPlanSpot SET mealId = null WHERE mealPlanDayId = :mealPlanDayId AND mealId = :mealId")
    suspend fun removeMealFromMealPlanSpot(mealPlanDayId: String, mealId: String)
}