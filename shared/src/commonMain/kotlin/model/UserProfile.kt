package model

import kotlinx.datetime.LocalDateTime

class UserProfile(
    var uid: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var surgeryDate: LocalDateTime,
    var mainMeals: Int = 3,
    var betweenMeals: Int = 3,
    var profileImageURL: String?,
    var startWeight: Double,
    var waterIntake: Double,
    var waterDayIntake: Double,
)