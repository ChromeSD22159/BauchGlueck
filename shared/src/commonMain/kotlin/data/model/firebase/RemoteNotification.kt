package data.model.firebase

import kotlinx.serialization.Serializable

@Serializable
data class RemoteNotification(
    val token: String,
    val title: String,
    val body: String,
    val data: RemoteNotificationData,
)

