package data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CountdownTimerApiResponse(
    val data: List<TimerData>,
    val meta: Meta
)

@Serializable
data class TimerData(
    val id: Int,
    val attributes: TimerAttributes
)

@Serializable
data class TimerAttributes(
    val timerId: String,
    val userId: String,
    val name: String,
    val duration: String,
    val startDate: String? = null,
    val endDate: String? = null,
    val timerState: String,
    val showActivity: Boolean? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class Meta(
    val pagination: Pagination
)

@Serializable
data class Pagination(
    val page: Int,
    val pageSize: Int,
    val pageCount: Int,
    val total: Int
)
