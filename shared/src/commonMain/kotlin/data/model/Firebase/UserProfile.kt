package data.model.Firebase

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import util.toLocalDateTime
import util.toLong

@Serializable
data class UserProfile(
    var uid: String = "",
    val firstName: String = "",
    val email: String = "",
    var surgeryDateTimeStamp: Long = Clock.System.now().toEpochMilliseconds(),
    var mainMeals: Int = 3,
    val betweenMeals: Int = 3,
    var profileImageURL: String? = null,
    var startWeight: Double  = 100.0,
    val waterIntake: Double = 0.25,
    val waterDayIntake: Double = 2.0,
    val userNotifierToken: String = ""
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

    var totalMeals: Int
        get() = mainMeals + betweenMeals
        set(value) {
            mainMeals = value - betweenMeals
        }
}

