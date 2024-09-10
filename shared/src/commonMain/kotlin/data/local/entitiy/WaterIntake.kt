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
    var userId: String = "",
    var waterIntakeId: String = "",
    var value: Double = 0.0,
    var isDeleted: Boolean = false,
    var updatedAtOnDevice: Long = Clock.System.now().toEpochMilliseconds(),
)