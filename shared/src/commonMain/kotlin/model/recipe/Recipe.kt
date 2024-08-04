package model.recipe

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Int = 0,
    var userID: String,
    var title: String,
    var recipeCategory: RecipeCategory,
    var portionSize: String,
    var preparationTime: String,
    var cookingTime: String,
    var ingredients: List<Ingredient>,
    var preparation: String,
    var rating: Int,
    var notes: String,
    var titleImage: String,
    var images: List<String>,
    var isPrivate: Boolean,
    var created: String,
    var lastUpdated: String,
)