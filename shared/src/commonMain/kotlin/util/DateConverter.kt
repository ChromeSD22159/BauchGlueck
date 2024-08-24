package util

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class DateConverter {
    @TypeConverter
    fun fromInstant(value: Instant?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toTimestamp(value: String?): Long? {
        return value?.let { Instant.parse(it).toEpochMilliseconds() }
    }

    fun formatTimeToMMSS(s: Long): String {
        val duration = s.toDuration(DurationUnit.SECONDS)
        val minutes = duration.inWholeMinutes
        val seconds = duration.inWholeSeconds % 60
        val minutesString = if (minutes < 10) "0$minutes" else "$minutes"
        val secondsString = if (seconds < 10) "0$seconds" else "$seconds"

        return "$minutesString:$secondsString"
    }
}

fun Long.toIsoDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return instant.toString()
}