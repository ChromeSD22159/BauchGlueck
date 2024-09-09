package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import data.local.entitiy.MealCategory
import data.local.entitiy.CategoryWithMeals
import data.local.entitiy.Meal
import data.local.entitiy.MealCategoryCrossRef
import data.local.entitiy.MealWithCategories
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMeal(meal: Meal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(mealCategory: MealCategory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealCategoryCrossRef(crossRef: MealCategoryCrossRef)

    @Query("SELECT * FROM meals WHERE mealId = :mealId")
    suspend fun getMealById(mealId: String): Meal?

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: String): MealCategory?

    // Holen aller Kategorien mit ihren jeweiligen Rezepten
    @Transaction
    @Query("SELECT * FROM categories WHERE categoryId = :categoryId")
    fun getCategoryWithMeals(categoryId: String): Flow<List<CategoryWithMeals>>

    // Holen aller Rezepte mit ihren jeweiligen Kategorien
    @Transaction
    @Query("SELECT * FROM meals")
    fun getMealWithCategories(): Flow<List<MealWithCategories>>

    @Query("SELECT * FROM meals WHERE updatedAtOnDevice > :timeStamp")
    suspend fun getMealsWithCategoriesAfterTimeStamp(timeStamp: Long): List<MealWithCategories>
}