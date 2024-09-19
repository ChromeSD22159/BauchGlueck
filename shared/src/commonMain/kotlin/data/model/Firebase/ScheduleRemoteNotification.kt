package data.model.Firebase

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleRemoteNotification(
    var token: String,
    val title: String,
    val body: String,
    val data: RemoteNotificationData,
    val scheduledTime: String,
)