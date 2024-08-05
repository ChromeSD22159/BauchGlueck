package database

import androidx.room.Entity
import androidx.room.PrimaryKey

// For RecipeCategory
@Entity(tableName = "categories")
data class RecipeCategoryEntity(
    @PrimaryKey val id: Int,
    val displayName: String
)

// For Ingredient (optional, based on Recipe's isPrivate)
@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey val id: Int,
    val value: String,
    val name: String,
    val formId: Int?,
    val unitId: Int
)

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: Int,
    val userID: String,
    val title: String,
    val recipeCategoryId: Int,
    val portionSize: String,
    val preparationTime: String,
    val cookingTime: String,
    val preparation: String,
    val rating: Int,
    val notes: String,
    val titleImage: String,
    val isPrivate: Boolean,
    val created: String,
    val lastUpdated: String
)

// For MeasurementUnit
@Entity(tableName = "units")
data class MeasurementUnitEntity(
    @PrimaryKey val id: Int,
    val displayName: String,
    val symbol: String
)

