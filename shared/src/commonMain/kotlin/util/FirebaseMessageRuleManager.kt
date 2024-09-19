package util

import data.model.Firebase.RemoteNotificationData
import data.model.Firebase.ScheduleRecurringRemoteNotification
import kotlinx.datetime.TimeZone

object FirebaseMessageRuleManager {

    // Funktion zum Erstellen einer Regel im Cron-Stil und eines ScheduleRecurringRemoteNotification-Objekts
    fun createScheduledNotification(
        token: String,
        title: String,
        body: String,
        data: RemoteNotificationData,
        minute: Int = -1,
        hour: Int = -1,
        dayOfMonth: Int = -1,
        month: Int = -1,
        dayOfWeek: Int = -1
    ): ScheduleRecurringRemoteNotification {
        val cronExpression = buildCronExpression(minute, hour, dayOfMonth, month, dayOfWeek)
        return ScheduleRecurringRemoteNotification(
            token = token,
            title = title,
            body = body,
            data = data,
            rule = cronExpression
        )
    }

    // Hilfsfunktion zum Erstellen des Cron-Ausdrucks
    private fun buildCronExpression(
        minute: Int,
        hour: Int,
        dayOfMonth: Int,
        month: Int,
        dayOfWeek: Int
    ): String {
        return "${formatCronField(minute)} ${formatCronField(hour)} ${formatCronField(dayOfMonth)} ${formatCronField(month)} ${formatCronField(dayOfWeek)}"
    }

    // Hilfsfunktion zum Formatieren der Felder
    private fun formatCronField(value: Int): String {
        return if (value in 0..59) value.toString() else "*"
    }

    // Funktion zum Erstellen einer täglichen Benachrichtigung
    fun createDailyNotification(
        token: String,
        title: String,
        body: String,
        data: RemoteNotificationData,
        hour: Int,
        minute: Int,
        timeZone: TimeZone = TimeZone.UTC
    ): ScheduleRecurringRemoteNotification {
        return createScheduledNotification(token, title, body, data, minute, hour)
    }

    // Funktion zum Erstellen einer wöchentlichen Benachrichtigung
    fun createWeeklyNotification(
        token: String,
        title: String,
        body: String,
        data: RemoteNotificationData,
        dayOfWeek: Int, // 0 = Sonntag, 1 = Montag, ... 6 = Samstag
        hour: Int,
        minute: Int,
        timeZone: TimeZone = TimeZone.UTC
    ): ScheduleRecurringRemoteNotification {
        return createScheduledNotification(token, title, body, data, minute, hour, dayOfWeek = dayOfWeek)
    }

    // Funktion zum Erstellen einer monatlichen Benachrichtigung
    fun createMonthlyNotification(
        token: String,
        title: String,
        body: String,
        data: RemoteNotificationData,
        dayOfMonth: Int,
        hour: Int,
        minute: Int,
        timeZone: TimeZone = TimeZone.UTC
    ): ScheduleRecurringRemoteNotification {
        return createScheduledNotification(token, title, body, data, minute, hour, dayOfMonth = dayOfMonth)
    }
}