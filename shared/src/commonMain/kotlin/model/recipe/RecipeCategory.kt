package model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RecipeCategory(
    var id: Int,
    var displayName: String
)