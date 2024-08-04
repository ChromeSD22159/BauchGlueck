package model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class IngredientForm(
    var id: Int,
    var displayName: String
)