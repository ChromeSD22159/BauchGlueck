package util

import androidx.room.TypeConverter
import kotlinx.datetime.Instant


class DateConverter {
    @TypeConverter
    fun fromInstant(value: Instant?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toTimestamp(value: String?): Long? {
        return value?.let { Instant.parse(it).toEpochMilliseconds() }
    }
}