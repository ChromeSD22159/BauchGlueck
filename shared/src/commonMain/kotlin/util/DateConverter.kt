package util

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class DateConverter {
    fun formatTimeToMMSS(s: Long): String {
        val duration = s.toDuration(DurationUnit.SECONDS)
        val minutes = duration.inWholeMinutes
        val seconds = duration.inWholeSeconds % 60
        val minutesString = if (minutes < 10) "0$minutes" else "$minutes"
        val secondsString = if (seconds < 10) "0$seconds" else "$seconds"

        return "$minutesString:$secondsString"
    }
}

fun Long.formatTimeToMMSS(): String {
    val duration = this.toDuration(DurationUnit.SECONDS)
    val minutes = duration.inWholeMinutes
    val seconds = duration.inWholeSeconds % 60
    val minutesString = if (minutes < 10) "0$minutes" else "$minutes"
    val secondsString = if (seconds < 10) "0$seconds" else "$seconds"

    return "$minutesString:$secondsString"
}

fun Long.toIsoDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return instant.toString()
}

fun String.toLocalDate(): LocalDate {
    val instant = Instant.parse(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return localDateTime.date
}

fun LocalDateTime.toLong(): Long {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    return instant.toEpochMilliseconds()
}

fun Long.toLocalDateTime(): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(this)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}