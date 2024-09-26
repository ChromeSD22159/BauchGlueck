package data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeeklyAverage(
    val avgValue: Double,
    val week: String
)