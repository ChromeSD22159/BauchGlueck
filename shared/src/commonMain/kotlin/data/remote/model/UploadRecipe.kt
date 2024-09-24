package data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeUpload(
    val updatedAtOnDevice: Long,
    val mealId: String,
    val userId: String,
    val description: String,
    val isDeleted: Boolean,
    val isPrivate: Boolean,
    val isSnack: Boolean,
    val name: String,
    val preparation: String,
    val preparationTimeInMinutes: String,
    val ingredients: List<Ingredient>,
    val mainImage: MainImageUpload,
    val category: CategoryUpload,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val sugar : Double = 0.0,
    val kcal: Double = 0.0
)

@Serializable
data class MainImageUpload(
    val id: Int
)

@Serializable
data class CategoryUpload(
    val categoryId: String,
    val name: String
)