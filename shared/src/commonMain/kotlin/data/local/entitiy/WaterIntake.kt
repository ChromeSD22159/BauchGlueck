package data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.DateConverter

@Serializable
@Entity(tableName = "water_intake")
data class WaterIntake(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerialName("userId")
    var userId: String = "",

    @SerialName("waterIntakeId")
    var waterIntakeId: String = "",

    @SerialName("value")
    val value: Double = 0.0,

    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @SerialName("updatedAtOnDevice")
    var updatedAtOnDevice: Long = Clock.System.now().toEpochMilliseconds(),

    @SerialName("createdAt")
    @TypeConverters(DateConverter::class)
    var createdAt: Long = Clock.System.now().toEpochMilliseconds(),

    @SerialName("updatedAt")
    @TypeConverters(DateConverter::class)
    var updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)