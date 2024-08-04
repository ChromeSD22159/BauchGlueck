package de.frederikkohler.bauchglueck.model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class MeasurementUnit(
    val id: Int? = null,
    val displayName: String,
    val symbol: String
)