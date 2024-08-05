package model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class IngredientForm(
    val id: Int = 0,
    var displayName: String
)