package ui.navigations

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

sealed class NavKeys(val key: String) {
    data object RecipeId : NavKeys("recipeId")
    data object UserId : NavKeys("userId")
    data object MealId : NavKeys("mealId")
    data object MealPlanId : NavKeys("mealPlanId")
    data object MealPlanSpotId : NavKeys("mealPlanSpotId")
    data object MealPlanDayId : NavKeys("mealPlanDayId")
    data object RecipeCategory : NavKeys("recipeCategory")
    data object Destination: NavKeys("destination")
    data object Date: NavKeys("date")
    data class CustomKey(val customId: String) : NavKeys(customId)
}

fun NavHostController.setNavKey(key: NavKeys, value: String) {
    this.currentBackStackEntry?.savedStateHandle?.set(key.key, value)
}

fun NavBackStackEntry.getNavKey(key: NavKeys): String? {
    return this.savedStateHandle.get<String>(key.key)
}