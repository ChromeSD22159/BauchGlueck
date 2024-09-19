package data.model

data class ScheduleRemoteNotification(
    val token: String,
    val title: String,
    val body: String,
    val data: RemoteNotificationData,
    val scheduledTime: String,
)