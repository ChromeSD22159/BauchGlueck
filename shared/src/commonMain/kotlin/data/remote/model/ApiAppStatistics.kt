package data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiAppStatistics(
    val mealPlan: MealPlanStatistics,
    val medications: Medications,
    val recipes: Recipes,
    val totalEntries: Int = 0,
    val timer: Timer,
    val userRelated: UserRelated,
    val weights: Weights,
    val waterIntake: WaterIntakeStatistics
)

@Serializable
data class MealPlanStatistics(
    val totalMealPlansSpots: Int? = 0,
    val totalMealPlans: Int? = 0
)

@Serializable
data class Medications(
    val totalMedication: Int? = 0,
    val totalIntakeTimes: Int? = 0,
    val totalIntakeStatus: Int? = 0
)

@Serializable
data class Recipes(
    val totalRecipes: Int? = 0
)

@Serializable
data class Timer(
    val countdownTimerTotalEntries: Int? = 0
)

@Serializable
data class UserRelated(
    val avgWeightPerUser: Double? = 0.0,
    val avgDurationPerUser: Double? = 0.0,
    val avgTimerPerUser: Double? = 0.0,
    val avgStatusPerUser: Double? = 0.0
)

@Serializable
data class Weights(
    val weightsEntries: Int = 0
)

@Serializable
data class WaterIntakeStatistics(
    val waterIntakesEntries: Int = 0
)