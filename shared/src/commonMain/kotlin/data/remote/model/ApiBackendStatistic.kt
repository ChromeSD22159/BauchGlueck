package data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiBackendStatistic(
    val mealPlan: MealPlanStatistic,
    val medications: MedicationsStatistic,
    val recipes: RecipesStatistic,
    val totalEntries: Long,
    val timer: TimerStatistic,
    val userRelated: UserRelatedStatistic,
    val weights: WeightsStatistic,
    val waterIntake: WaterIntakeStatistic,
)

@Serializable
data class MealPlanStatistic(
    val totalMealPlansSpots: Long,
    val totalMealPlans: Long,
)

@Serializable
data class MedicationsStatistic(
    val totalMedication: Long,
    val totalIntakeTimes: Long,
    val totalIntakeStatus: Long,
)

@Serializable
data class RecipesStatistic(
    val totalMeal: Long,
)

@Serializable
data class TimerStatistic(
    val countdownTimerTotalEntries: Long,
)

@Serializable
data class UserRelatedStatistic(
    val avgWeightPerUser: Long,
    val avgDurationPerUser: Long,
    val avgTimerPerUser: Double,
    val avgStatusPerUser: Double,
)

@Serializable
data class WeightsStatistic(
    val weightsEntries: Long,
)

@Serializable
data class WaterIntakeStatistic(
    val waterIntakesEntries: Long,
)
