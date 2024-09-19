package util

import data.model.Firebase.RemoteNotificationData
import data.model.Firebase.ScheduleRemoteNotification
import viewModel.toUTC

enum class Notification {
    FinishedTimer
}

data object Notifications {
    private var list: Map<Notification, ScheduleRemoteNotification> = mapOf(
        Notification.FinishedTimer to ScheduleRemoteNotification(
            token = "",
            title = "BauchGlück Notification",
            body = "Dein {timerName} ist abgelaufen!}",
            data = RemoteNotificationData(
                key1 = "",
                key2 = "",
            ),
            scheduledTime = "{trigger}"
        )
    )

    fun getScheduleRemoteNotification(notification: Notification): ScheduleRemoteNotification? {
        return list[notification]
    }

    fun ScheduleRemoteNotification.generate(token: String, timerName: String, trigger: Long): ScheduleRemoteNotification {
        return this.copy(
            token= token,
            body = this.body.replace("{timerName}", timerName),
            scheduledTime = this.scheduledTime.replace("{trigger}", trigger.toUTC())
        )
    }
}