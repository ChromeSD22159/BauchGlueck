package data.model.firebase

import kotlinx.serialization.Serializable

@Serializable
data class RemoteNotificationData(
    val key1: String,
    val key2: String,
)