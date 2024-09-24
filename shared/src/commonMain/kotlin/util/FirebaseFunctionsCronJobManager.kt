package util

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * ```
val cronJobManager = FirebaseFunctionsCronJobManager()

// Example usage:
// Create a cron job with identifier for every day at 8:00 AM
val cronJob = cronJobManager.generateCronJob(
identifier = "daily_morning_medication",
minutes = listOf(0),
hours = listOf(8)
)

val notificationDetails = NotificationDetails(
title = "Medikamentenerinnerung",
message = "Vergiss nicht, dein Medikament um 8:00 Uhr einzunehmen!",
data = mapOf("medicationId" to "12345"),
scheduledTime = "2024-09-21T08:00:00Z" // Optional scheduled time in ISO 8601 format
)

val request = NotificationCronJobRequest(
notification = notificationDetails,
cronJob = cronJob
)
 * }
 * ```
 */
object FirebaseFunctionsCronJobManager {
    /**
     * Generates a cron job with identifier based on provided parameters.
     *
     * @param identifier Unique identifier for the cron job.
     * @param minutes List of minutes at which the job should run (0-59). If empty, uses "*".
     * @param hours List of hours at which the job should run (0-23). If empty, uses "*".
     * @param daysOfMonth List of days of the month on which the job should run (1-31). If empty, uses "*".
     * @param months List of months in which the job should run (1-12). If empty, uses "*".
     * @param daysOfWeek List of days of the week on which the job should run (0-6, 0 = Sunday). If empty, uses "*".
     * @param intervalMinutes Interval in minutes for the cron job to execute. Overrides all other parameters if provided.
     * @return A CronJob object containing the identifier and cron expression.
     */
    fun generateCronJob(
        identifier: String,
        minutes: List<Int> = emptyList(),
        hours: List<Int> = emptyList(),
        daysOfMonth: List<Int> = emptyList(),
        months: List<Int> = emptyList(),
        daysOfWeek: List<Int> = emptyList(),
        intervalMinutes: Int? = null,
        oneTimeSchedule: String? = null
    ): CronJobDetails {
        return if (oneTimeSchedule != null) {
            // If oneTimeSchedule is provided, create a one-time job
            CronJobDetails(
                identifier = identifier,
                cronExpression = oneTimeSchedule, // Using cronExpression field to store the one-time datetime
                isRecurring = false // Not a recurring job
            )
        } else {
            // Generate cron expression for recurring job
            val cronExpression = if (intervalMinutes != null) {
                // Generate a cron expression for every intervalMinutes.
                "*/$intervalMinutes * * * *"
            } else {
                // Join list values with commas, use "*" if list is empty.
                val minutePart = if (minutes.isEmpty()) "*" else minutes.joinToString(",")
                val hourPart = if (hours.isEmpty()) "*" else hours.joinToString(",")
                val dayOfMonthPart = if (daysOfMonth.isEmpty()) "*" else daysOfMonth.joinToString(",")
                val monthPart = if (months.isEmpty()) "*" else months.joinToString(",")
                val dayOfWeekPart = if (daysOfWeek.isEmpty()) "*" else daysOfWeek.joinToString(",")

                "$minutePart $hourPart $dayOfMonthPart $monthPart $dayOfWeekPart"
            }

            CronJobDetails(
                identifier = identifier,
                cronExpression = cronExpression,
                isRecurring = true // This is a recurring job
            )
        }
    }

    /**
     * Updates an existing cron job with new parameters.
     *
     * @param identifier Unique identifier for the cron job to be updated.
     * @param newCronExpression New cron expression to update the job with.
     * @return A CronJobDetails object with updated information.
     */
    fun updateCronJob(
        identifier: String,
        newCronExpression: String
    ): CronJobDetails {
        // Assuming we need to send an update request to Strapi or Firebase
        // Here we just return the updated details
        return CronJobDetails(
            identifier = identifier,
            cronExpression = newCronExpression,
            isRecurring = true // Assuming the job is still recurring
        )
    }

    /**
     * Deletes an existing cron job.
     *
     * @param identifier Unique identifier for the cron job to be deleted.
     * @return A boolean indicating success or failure.
     */
    fun deleteCronJob(identifier: String): Boolean {
        // Assuming we need to send a delete request to Strapi or Firebase
        // Here we just return true for successful deletion
        println("Deleting cron job with identifier: $identifier")
        return true
    }

    /**
     * Sends a notification immediately.
     *
     * @param notification The notification details to be sent.
     * @return A boolean indicating success or failure.
     */
    fun sendNotificationImmediately(notification: NotificationDetails): Boolean {
        // Assuming we need to send a request to Firebase or another service
        println("Sending notification immediately: ${notification.title}, ${notification.body}")
        // Placeholder for actual send logic
        return true
    }
}

@Serializable
data class CronJob(
    val identifier: String,
    val cronExpression: String
)

@Serializable
data class CronJobDetails(
    val identifier: String, // Unique identifier for the cron job
    val cronExpression: String, // Cron expression to schedule the job
    val isRecurring: Boolean, // Flag to indicate if the job is recurring or a one-time event
    val startDate: String? = null, // Optional start date (ISO 8601 format)
    val endDate: String? = null // Optional end date (ISO 8601 format)
)

@Serializable
data class NotificationDetails(
    var token: String,
    val title: String,
    val body: String,
    val data: Map<String, String> = emptyMap(),
)

@Serializable
data class NotificationCronJobRequest(
    val notification: NotificationDetails, // Notification details
    val cronJob: CronJobDetails? = null, // Cron job details
)

@Serializable
data class FirebaseCloudMessagingResponse(
    @SerialName("message") val message: String ?= null,
    @SerialName("response") val response: String ? = null,
)