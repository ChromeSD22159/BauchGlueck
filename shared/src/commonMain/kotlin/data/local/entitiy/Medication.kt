package data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.DateConverter

@Serializable
@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerialName("userId")
    var userId: String = "",

    @SerialName("name")
    var name: String = "",

    @SerialName("dosage")
    var dosage: String = "",

    @SerialName("intakeTimes")
    var intakeTimes: String = "",

    @SerialName("medicationPlanId")
    var medicationPlanId: Int? = null,

    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @SerialName("createdAt")
    @TypeConverters(DateConverter::class)
    var createdAt: Long = Clock.System.now().toEpochMilliseconds(),

    @SerialName("updatedAt")
    @TypeConverters(DateConverter::class)
    var updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)