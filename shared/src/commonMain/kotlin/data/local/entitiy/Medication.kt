package data.local.entitiy

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Relation
import androidx.room.TypeConverters
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import util.DateConverter
import util.generateDeviceId

@Serializable
@Entity(
    primaryKeys = ["medicationId"],
)
data class Medication(
    var id: Int = 0,

    var medicationId: String = generateDeviceId(),

    var userId: String = "",

    var name: String = "",

    var dosage: String = "",

    var isDeleted: Boolean = false,

    var updatedAtOnDevice: Long = Clock.System.now().toEpochMilliseconds()
)

@Serializable
@Entity(
    primaryKeys = ["intakeTimeId"],
    foreignKeys = [ForeignKey(
        entity = Medication::class,
        parentColumns = ["medicationId"],
        childColumns = ["medicationId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("medicationId")]
)
data class IntakeTime(
    var intakeTimeId: String = generateDeviceId(),

    var intakeTime: String = Clock.System.now().toString(),

    var medicationId: String = "",

    var isDeleted: Boolean = false,

    var updatedAtOnDevice: Long = Clock.System.now().toEpochMilliseconds()
)

@Serializable
@Entity(
    primaryKeys = ["intakeStatusId"],
    foreignKeys = [ForeignKey(
        entity = IntakeTime::class,
        parentColumns = ["intakeTimeId"],
        childColumns = ["intakeTimeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("intakeTimeId")]
)
data class IntakeStatus(
    var intakeStatusId: String = generateDeviceId(),

    var intakeTimeId: String = "",

    @TypeConverters(DateConverter::class)
    var date: Long = Clock.System.now().toEpochMilliseconds(),

    var isTaken: Boolean = false,

    var isDeleted: Boolean = false,

    var updatedAtOnDevice: Long = Clock.System.now().toEpochMilliseconds()
)

@Serializable
data class MedicationWithIntakeDetails(
    @Embedded val medication: Medication,

    @Relation(
        parentColumn = "medicationId",
        entityColumn = "medicationId"
    )
    val intakeTimesWithStatus: List<IntakeTimeWithStatus>
)

@Serializable
data class IntakeTimeWithStatus(
    @Embedded val intakeTime: IntakeTime,

    @Relation(
        parentColumn = "intakeTimeId",
        entityColumn = "intakeTimeId"
    )
    var intakeStatuses: List<IntakeStatus>
)

@Serializable
data class MedicationWithIntakeDetailsForToday(
    @Embedded val medication: Medication,

    @Relation(
        entity = IntakeTime::class,
        parentColumn = "medicationId",
        entityColumn = "medicationId"
    )
    val intakeTimesWithStatus: List<IntakeTimeWithStatus>
)

@Serializable
data class MedicationIntakeDataAfterTimeStamp(
    @Embedded val medication: Medication,

    @Relation(
        entity = IntakeTime::class,
        parentColumn = "medicationId",
        entityColumn = "medicationId"
    )
    val intakeTimesWithStatus: List<IntakeTimeWithStatus>
)

@Serializable
data class MedicationIntakeData(
    @Embedded val medication: Medication,

    @Relation(
        entity = IntakeTime::class,
        parentColumn = "medicationId",
        entityColumn = "medicationId"
    )
    val intakeTimesWithStatus: List<IntakeTimeWithStatus>
)