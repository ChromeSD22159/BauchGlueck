package data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import util.DateConverter

@Serializable
@Entity
data class CountdownTimer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var timerId: String = "",
    var userId: String = "",
    var name: String = "",
    var duration: Long = 0,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var timerState: String = "",
    var showActivity: Boolean = true,
    var isDeleted: Boolean = false,
    var updatedAtOnDevice: Long = Clock.System.now().toEpochMilliseconds(),
    @TypeConverters(DateConverter::class)
    var createdAt: String = Clock.System.now().toString(),
    @TypeConverters(DateConverter::class)
    var updatedAt: String = Clock.System.now().toString(),
) {
    var toCreatedAtLong: Long = Instant.parse(createdAt).toEpochMilliseconds()

    var toUpdateAtLong: Long = Instant.parse(updatedAt).toEpochMilliseconds()

    var timerStateEnum: TimerState
        get() = TimerState.fromValue(this.timerState)
        set(value) { timerState = value.value }
}

enum class TimerState(val value: String) {
    running("running"),
    paused("paused"),
    completed("completed"),
    notRunning("notRunning");

    companion object {
        fun fromValue(value: String): TimerState {
            return entries.find { it.value == value } ?: notRunning
        }
    }
}
