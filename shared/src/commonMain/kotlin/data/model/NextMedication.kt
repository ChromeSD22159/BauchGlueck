package data.model

import data.local.entitiy.Medication
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class NextMedication(
    val medication: Medication,
    val intakeTime: LocalDateTime
)