package data.remote.model

import data.local.entitiy.CountdownTimer
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable


@Serializable
data class CountdownTimerAttributes(
    val timerId: String,
    val userId: String,
    val name: String,
    val duration: String,
    val startDate: String? = null,
    val endDate: String? = null,
    val timerState: String,
    val showActivity: Boolean,
    val createdAt: String,
    val updatedAt: String
) {
    fun toCountdownTimer(): CountdownTimer {
        return CountdownTimer(
            timerId = timerId,
            userId = userId,
            name = name,
            duration = duration.toLong(),
            startDate = startDate?.toLong(),
            endDate = endDate?.toLong(),
            timerState = timerState,
            showActivity = showActivity,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}