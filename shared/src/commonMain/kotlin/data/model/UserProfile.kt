package data.model

import kotlinx.datetime.*

data class UserProfile(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    var surgeryDateTimeStamp: Long,
    val mainMeals: Int,
    val betweenMeals: Int,
    var profileImageURL: String?,
    var startWeight: Double,
    val waterIntake: Double,
    val waterDayIntake: Double
) {
    var surgeryDate: LocalDateTime
        get() = convertLongToLocalDateTime(surgeryDateTimeStamp)
        set(value) {
            surgeryDateTimeStamp = convertLocalDateTimeToLong(value)
        }

    fun updateSurgeryDate(newDate: LocalDateTime) {
        surgeryDateTimeStamp = convertLocalDateTimeToLong(newDate)
    }

    fun updateSurgeryDate(newDateLong: Long) {
        surgeryDateTimeStamp = newDateLong
    }
}



fun convertLongToLocalDateTime(timestampMillis: Long): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(timestampMillis)
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

fun convertLocalDateTimeToLong(localDateTime: LocalDateTime): Long {
    val instant = localDateTime.toInstant(TimeZone.currentSystemDefault())
    return instant.toEpochMilliseconds()
}