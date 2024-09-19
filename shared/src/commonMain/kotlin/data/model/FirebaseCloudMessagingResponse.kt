package data.model

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseCloudMessagingResponse(
    val message: String,
    val response: String,
)

@Serializable
data class FirebaseCloudMessagingScheduledTimeResponse(
    val message: String,
    val scheduledTime: String,
)