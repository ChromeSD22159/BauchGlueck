package data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.DateConverter

@Serializable
@Entity
data class CountdownTimer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("timerId")
    var timerId: String = "",
    @SerialName("userId")
    var userId: String = "",
    @SerialName("name")
    var name: String = "",
    @SerialName("duration")
    var duration: Long = 0,
    @SerialName("startDate")
    var startDate: Long? = null,
    @SerialName("endDate")
    var endDate: Long? = null,
    @SerialName("timerState")
    var timerState: String = "",
    @SerialName("showActivity")
    var showActivity: Boolean = true,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,
    @SerialName("createdAt")
    @TypeConverters(DateConverter::class)
    var createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    @SerialName("updatedAt")
    @TypeConverters(DateConverter::class)
    var updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
)

