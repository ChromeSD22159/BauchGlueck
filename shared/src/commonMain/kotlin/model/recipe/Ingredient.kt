package model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    var id: Int,
    var value: String,
    var name: String,
    var form: IngredientForm? = null,
    var unit: MeasurementUnit,
)