package model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val id: Int = 0,
    var value: String,
    var name: String,
    var form: IngredientForm? = null,
    var unit: MeasurementUnit,
)