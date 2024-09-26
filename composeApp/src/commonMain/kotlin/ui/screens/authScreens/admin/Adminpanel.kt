package ui.screens.authScreens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import data.remote.StrapiApiClient
import data.remote.model.ApiBackendStatistic
import kotlinx.coroutines.delay
import okhttp3.internal.wait
import ui.components.theme.ScreenHolder
import ui.components.theme.text.BodyText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import util.onError
import util.onSuccess

fun NavGraphBuilder.adminPanelComposable(
    navController: NavHostController
) {
    composable(Destination.AdminPanel.route) {
        val remote = StrapiApiClient()
        val backendStatistics = remember {
            mutableStateOf<ApiBackendStatistic?>(null)
        }
        val isLoading = remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            isLoading.value = true
            val result = remote.getBackendStatistics()
            result.onSuccess {
                backendStatistics.value = it
                delay(1000)
                isLoading.value = false
            }.onError {
                isLoading.value = false
            }
        }
        ScreenHolder(
            title = Destination.AdminPanel.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.Settings.route)
            }
        ) {
            if(isLoading.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                backendStatistics.value?.let {
                    Column {
                        HeadlineText(size = 18.sp, text = "MealPlan Statistics")
                        StatisticItem(item = "totalMealPlansSpots", value = it.mealPlan.totalMealPlansSpots)
                        StatisticItem(item = "totalMealPlans", value = it.mealPlan.totalMealPlans)
                    }

                    Column {
                        HeadlineText(size = 18.sp, text = "Medications Statistics")
                        StatisticItem(item = "avgWeightPerUser", value = it.medications.totalMedication)
                        StatisticItem(item = "avgWeightPerUser", value = it.medications.totalIntakeTimes)
                        StatisticItem(item = "avgWeightPerUser", value = it.medications.totalIntakeStatus)
                        StatisticItem(item = "avgWeightPerUser", value = it.medications.totalIntakeStatus)
                    }

                    Column {
                        HeadlineText(size = 18.sp, text = "UserRelated Statistics")
                        StatisticItem(item = "avgWeightPerUser", value = it.userRelated.avgWeightPerUser)
                        StatisticItem(item = "avgDurationPerUser", value = it.userRelated.avgDurationPerUser)
                        StatisticItem(item = "avgTimerPerUser", value = it.userRelated.avgTimerPerUser.toLong())
                        StatisticItem(item = "avgStatusPerUser", value = it.userRelated.avgStatusPerUser.toLong())
                    }

                    Column {
                        HeadlineText(size = 18.sp, text = "Recipes Statistics")
                        StatisticItem(item = "totalMeal", value = it.recipes.totalMeal)
                    }

                    Column {
                        HeadlineText(size = 18.sp, text = "CountdownTimer Statistics")
                        StatisticItem(item = "totalMeal", value = it.timer.countdownTimerTotalEntries)
                    }

                    Column {
                        HeadlineText(size = 18.sp, text = "Weight Statistics")
                        StatisticItem(item = "weightsEntries", value = it.weights.weightsEntries)
                    }

                    Column {
                        HeadlineText(size = 18.sp, text = "WaterIntakes Statistics")
                        StatisticItem(item = "waterIntakesEntries", value = it.waterIntake.waterIntakesEntries)
                    }

                    Column {
                        HeadlineText(size = 18.sp, text = "TotalEntries Statistics")
                        StatisticItem(item = "totalEntries", value = it.totalEntries)
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticItem(
    item: String,
    value: Long
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BodyText(text = item)
        BodyText(text = value.toString())
    }
}