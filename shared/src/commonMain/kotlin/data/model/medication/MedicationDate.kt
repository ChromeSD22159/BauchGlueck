package data.model.medication

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import util.StartEndOfDay

@Serializable
data class MedicationDate(
    val date: LocalDateTime,
    val startEndToday: StartEndOfDay,
    val intakePercentage: Double
) {
    val percentage: Float
        get() = (intakePercentage / 100.0).toFloat()
}