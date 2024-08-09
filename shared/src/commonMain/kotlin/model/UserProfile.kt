package model

data class UserProfile(
    val uid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val surgeryDate: Long,
    val mainMeals: Int,
    val betweenMeals: Int,
    var profileImageURL: String?,
    val startWeight: Double,
    val waterIntake: Double,
    val waterDayIntake: Double
)