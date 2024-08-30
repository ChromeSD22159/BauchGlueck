package data.local.entitiy

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverters
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.DateConverter

@Serializable
@Entity
data class Medication(

    val id: Int = 0,

    @SerialName("userId")
    var userId: String = "",

    @PrimaryKey
    @SerialName("medicationId")
    var medicationId: String = "",

    @SerialName("name")
    val name: String = "Medikamenten Namen",

    @SerialName("dosage")
    val dosage: String = "z.B. 200mg",

    @SerialName("isDeleted")
    var isDeleted: Boolean = false,

    @SerialName("updatedAtOnDevice")
    @TypeConverters(DateConverter::class)
    var updatedAtOnDevice: Long = Clock.System.now().toEpochMilliseconds(),


    @SerialName("createdAt")
    @TypeConverters(DateConverter::class)
    var createdAt: Long = Clock.System.now().toEpochMilliseconds(),

    @SerialName("updatedAt")
    @TypeConverters(DateConverter::class)
    var updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Medication::class,
        parentColumns = ["medicationId"],
        childColumns = ["id"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class IntakeTimes(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val intakeTime: String = "08:00",

    @SerialName("token")
    @TypeConverters(DateConverter::class)
    var token: Long = Clock.System.now().toEpochMilliseconds(),

    @SerialName("medicationId")
    var medicationId: String = "",

    @SerialName("isDeleted")
    var isDeleted: Boolean = false,

    @SerialName("createdAt")
    @TypeConverters(DateConverter::class)
    var createdAt: String = Clock.System.now().toString(),

    @SerialName("updatedAt")
    @TypeConverters(DateConverter::class)
    var updatedAt: String = Clock.System.now().toString(),
)

data class MedicationWithIntakeTimes(
    @Embedded val medication: Medication,

    @Relation(
        parentColumn = "id",
        entityColumn = "medicationId"
    )
    val intakeTimes: List<IntakeTimes>
)