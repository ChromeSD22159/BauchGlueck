package util


enum class Notification {
    FinishedTimer
}

/*
data object Notifications {
    private var list: Map<Notification, NotificationDetails> = mapOf(
        Notification.FinishedTimer to ScheduleRemoteNotification(
            token = "",
            title = "BauchGlück Notification",
            body = "Dein {timerName} ist abgelaufen!",
            scheduledTime = "{trigger}"
        )
    )

    fun getScheduleRemoteNotification(notification: Notification): NotificationDetails? {
        return list[notification]
    }

    fun NotificationDetails.generate(token: String, timerName: String, trigger: Long): NotificationDetails {

        when(this) {
            is ScheduleRemoteNotification -> this.copy(
                token= token,
                body = this.body.replace("{timerName}", timerName),
                scheduledTime = this.scheduledTime.replace("{trigger}", trigger.toUTC())
            )

            is ScheduleRecurringRemoteNotification -> this.copy(
                token= token,
                body = this.body.replace("{timerName}", timerName),
            )
        }

        return this.copy(
            token= token,
            body = this.body.replace("{timerName}", timerName),
            scheduledTime = this.scheduledTime.replace("{trigger}", trigger.toUTC())
        )
    }
}
 */