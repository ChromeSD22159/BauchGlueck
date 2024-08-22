package data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class TimerSyncRequest(
    val timerId: String,
    val userId: String,
    val updatedAt: Long,
    val isDeleted: Boolean
)

@Serializable
data class TimerSyncResponse(
    val message: String,
    val deletedTimers: List<DeletedTimer>
)

@Serializable
data class DeletedTimer(
    val timerId: String,
    val userId: String
)