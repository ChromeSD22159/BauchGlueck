package data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class CountdownTimer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("timerId")
    var timerId: String = "",
    @SerialName("userId")
    var userId: String = "",
    @SerialName("deviceID")
    var deviceID: String = "",
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
    @SerialName("timerType")
    var timerType: String = "",
    @SerialName("remainingDuration")
    var remainingDuration: Long = 0,
    @SerialName("notification")
    var notification: Boolean = true,
    @SerialName("showActivity")
    var showActivity: Boolean = true,
    @SerialName("lastUpdate")
    var lastUpdate: Long? = null,
)
