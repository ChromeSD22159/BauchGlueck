package data.model.medication

import kotlinx.serialization.Serializable

@Serializable
data class MedicationHistory(
    val medicationName: String = "",
    val dosage: String = "",
    val medicationWeek: List<List<MedicationDate>> = emptyList()
) {
    val legend: List<Float>
        get() = medicationWeek.flatMap { date ->
            date.map { (it.intakePercentage / 100.0).toFloat() }
        }
        .distinct()
        .sorted()
}