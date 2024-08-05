package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface MeasurementUnitDao {
    @Query("SELECT * FROM units")
    suspend fun getAllUnits(): List<MeasurementUnitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: MeasurementUnitEntity)
}

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: IngredientEntity)

    @Query("DELETE FROM ingredients WHERE id = :id")
    suspend fun deleteIngredient(id: Int): Int
}

@Dao
interface RecipeCategoryDao {
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<RecipeCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<RecipeCategoryEntity>)
}

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<RecipeEntity>
}