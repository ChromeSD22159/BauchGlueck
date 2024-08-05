package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecipeCategoryEntity::class, MeasurementUnitEntity::class, RecipeEntity::class, IngredientEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeCategoryDao(): RecipeCategoryDao
    abstract fun measurementUnitDao(): MeasurementUnitDao
    abstract fun recipeDao(): RecipeDao
    abstract fun ingredientDao(): IngredientDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}