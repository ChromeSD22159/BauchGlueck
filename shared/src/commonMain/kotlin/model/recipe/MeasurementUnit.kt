package model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class MeasurementUnit(
    var id: Int,
    var displayName: String
)