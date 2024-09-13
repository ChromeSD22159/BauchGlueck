package data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import util.toLocalDateTime
import util.toLong

@Serializable
data class UserProfile(
    var uid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    var surgeryDateTimeStamp: Long = Clock.System.now().toEpochMilliseconds(),
    val mainMeals: Int = 3,
    val betweenMeals: Int = 3,
    var profileImageURL: String? = null,
    var startWeight: Double  = 100.0,
    val waterIntake: Double = 200.0,
    val waterDayIntake: Double = 2000.0
) {
    var surgeryDate: LocalDateTime
        get() = surgeryDateTimeStamp.toLocalDateTime()
        set(value) {
            surgeryDateTimeStamp = value.toLong()
        }

    fun updateSurgeryDate(newDate: LocalDateTime) {
        surgeryDateTimeStamp = newDate.toLong()
    }

    fun updateSurgeryDate(newDateLong: Long) {
        surgeryDateTimeStamp = newDateLong
    }
}

