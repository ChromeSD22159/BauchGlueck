package ui.screens.authScreens.mealPlan

sealed class KcalLevel(val kcal: Int) {
    data object Junior : KcalLevel(800)
    data object Mid : KcalLevel(1000)
    data object SeniorKcalLevel : KcalLevel(1200)
    data class CustomKcalLevel(val customKcal: Int) : KcalLevel(customKcal)
}