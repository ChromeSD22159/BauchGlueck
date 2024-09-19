package data.model.firebase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseCloudMessagingResponseModifyNotification(
    @SerialName("message") val message: String ?= null,
    @SerialName("notificationId") val notificationId: String ? = null,
)