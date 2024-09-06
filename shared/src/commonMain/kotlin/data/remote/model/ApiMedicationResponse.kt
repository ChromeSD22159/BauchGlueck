package data.remote.model

import data.local.entitiy.IntakeStatus
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ApiMedicationResponse(
    @SerialName("id")
    val id: Int,

    @SerialName("userId")
    val userId: String,

    @SerialName("name")
    val name: String,

    @SerialName("dosage")
    val dosage: String,

    @SerialName("isDeleted")
    val isDeleted: Boolean,

    @SerialName("medicationId")
    val medicationId: String,

    @SerialName("updatedAtOnDevice")
    val updatedAtOnDevice: Long,

    @SerialName("intake_times")
    val intakeTimes: List<IntakeTime>
) {
    fun toMedication(): data.local.entitiy.Medication {
        return data.local.entitiy.Medication(
            id = this.id,
            medicationId = this.medicationId,
            userId = this.userId,
            name = this.name,
            dosage = this.dosage,
            updatedAtOnDevice = this.updatedAtOnDevice,
            isDeleted = this.isDeleted
        )
    }
}

@kotlinx.serialization.Serializable
data class IntakeTime(
    @SerialName("id")
    val id: Int,

    @SerialName("intakeTime")
    val intakeTime: String,

    @SerialName("intakeTimeId")
    val intakeTimeId: String,

    @SerialName("updatedAtOnDevice")
    val updatedAtOnDevice: Long,

    @SerialName("intake_statuses")
    val intakeStatuses: List<IntakeStatus>
) {
    fun toIntakeTime(): data.local.entitiy.IntakeTime {
        return data.local.entitiy.IntakeTime(
            intakeTimeId = this.id.toString(),
            intakeTime = this.intakeTime,
            updatedAtOnDevice = this.updatedAtOnDevice
        )
    }
}

@kotlinx.serialization.Serializable
data class IntakeStatus(
    @SerialName("id")
    val id: Int,

    @SerialName("intakeStatusId")
    val intakeStatusId: String,

    @SerialName("date")
    val date: Long,

    @SerialName("isTaken")
    val isTaken: Boolean,

    @SerialName("updatedAtOnDevice")
    val updatedAtOnDevice: Long
) {
    fun toIntakeStatus(): data.local.entitiy.IntakeStatus {
        return data.local.entitiy.IntakeStatus(
            intakeStatusId = this.intakeStatusId,
            date = this.date,
            isTaken = this.isTaken,
            updatedAtOnDevice = this.updatedAtOnDevice
        )
    }
}