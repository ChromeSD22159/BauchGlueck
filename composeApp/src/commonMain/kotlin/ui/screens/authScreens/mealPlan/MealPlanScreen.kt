package ui.screens.authScreens.mealPlan

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_timer
import bauchglueck.composeapp.generated.resources.ic_fat
import bauchglueck.composeapp.generated.resources.ic_gear
import bauchglueck.composeapp.generated.resources.ic_info
import bauchglueck.composeapp.generated.resources.ic_kcal
import bauchglueck.composeapp.generated.resources.ic_person_fill_view
import bauchglueck.composeapp.generated.resources.ic_plus
import bauchglueck.composeapp.generated.resources.ic_protein
import bauchglueck.composeapp.generated.resources.ic_search
import bauchglueck.composeapp.generated.resources.ic_sugar
import data.local.entitiy.MealPlanDayWithSpots
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.ScreenHolder
import ui.components.theme.Section
import ui.components.theme.clickableWithRipple
import ui.components.extentions.onLongPress
import ui.components.extentions.sectionShadow
import ui.components.theme.text.BodyText
import ui.components.theme.text.HeadlineText
import ui.components.extentions.toDigits
import ui.components.theme.button.IconButton
import ui.components.theme.text.FooterText
import ui.navigations.Destination
import ui.navigations.NavKeys
import ui.navigations.getNavKey
import ui.navigations.setNavKey
import util.DateRepository
import util.debugJsonHelper
import util.parseToLocalDate
import util.toLong
import viewModel.FirebaseAuthViewModel
import viewModel.MealPlanViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mealPlan(
    navController: NavHostController,
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    composable(Destination.MealPlanCalendar.route) {backStackEntry ->
        val selectedRecipeId = backStackEntry.getNavKey(NavKeys.RecipeId)
        val selectedDateForRecipe = backStackEntry.savedStateHandle.get<String>(NavKeys.Date.key)?.parseToLocalDate()

        val viewModel = viewModel<MealPlanViewModel>()
        val userFormState by firebaseAuthViewModel.userFormState.collectAsStateWithLifecycle(initialValue = null)
        val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle(initialValue = DateRepository.today)

        val countOfMealsPerDay by viewModel.countOfMealsPerDay.collectAsState()
        val mealPlan by viewModel.mealPlanForSelectedDate.collectAsState()
        val shouldPlanedValue = userFormState?.userProfile?.value?.totalMeals ?: 0

        debugJsonHelper(mealPlan)

        // WORKS
        LaunchedEffect(Unit) {
            if (selectedRecipeId != null) {
                if (selectedDateForRecipe != null) {
                    viewModel.setSelectedDate(selectedDateForRecipe)
                    viewModel.addToMealPlan(selectedRecipeId, selectedDateForRecipe)
                } else {
                    viewModel.addToMealPlan(selectedRecipeId, selectedDate)
                }
            }
        }

        ScreenHolder(
            title = Destination.MealPlanCalendar.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.Home.route)
            },
            optionsRow = {
                Icon(
                    imageVector = vectorResource(resource = Res.drawable.ic_search),
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp)
                        .clickableWithRipple {
                            navController.navigate(Destination.SearchRecipe.route)
                            navController.setNavKey(
                                NavKeys.Destination,
                                Destination.MealPlanCalendar.route
                            )
                        },
                )

                Icon(
                    imageVector = vectorResource(resource = Res.drawable.ic_gear),
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp)
                        .clickableWithRipple { navController.navigate(Destination.Settings.route) },
                )
            },
        ) {

            HeaderRow(selectedDate, mealPlan)

            CalendarSlider(
                days = viewModel.calendarDays,
                selectedDate = selectedDate,
                isPlanedValue = countOfMealsPerDay,
                shouldPlanedValue = shouldPlanedValue
            ) { viewModel.setSelectedDate(it) }

            userFormState?.userProfile?.value?.let { user ->
                val kcalMealCount = mealPlan?.spots?.let { entries ->
                    entries.sumOf { mealObject -> mealObject.mealObject?.kcal ?: 0.0 }
                }
                val fatMealCount = mealPlan?.spots?.let { entries ->
                    entries.sumOf { mealObject -> mealObject.mealObject?.fat ?: 0.0 }
                }
                val proteinMealCount = mealPlan?.spots?.let { entries ->
                    entries.sumOf { mealObject -> mealObject.mealObject?.protein ?: 0.0 }
                }
                val sugarMealCount = mealPlan?.spots?.let { entries ->
                    entries.sumOf { mealObject -> mealObject.mealObject?.sugar ?: 0.0 }
                }
                OverViewSection(
                    operationTimestamp = user.surgeryDate.toLong(),
                    mealCount = (user.mainMeals + user.betweenMeals),
                    kcalMealCount = kcalMealCount?.toInt() ?: 0,
                    fatMealCount = fatMealCount?.toInt() ?: 0,
                    proteinMealCount = proteinMealCount?.toInt() ?: 0,
                    sugaredMealCount = sugarMealCount?.toInt() ?: 0
                )
            }

            userFormState?.userProfile?.value?.totalMeals?.let { totalMealsFromUserSettings ->
                for (i in 1..totalMealsFromUserSettings) {
                    val mealOrNull = mealPlan?.spots?.getOrNull(i - 1)
                    if (mealOrNull != null) {
                        MealCard(
                            index = i,
                            meal = mealOrNull.mealObject,
                            onClickAdd = {
                                navController.navigate(Destination.SearchRecipe.route)
                                navController.setNavKey(NavKeys.Destination, Destination.MealPlanCalendar.route)
                            },
                            onLongPress = {
                                viewModel.removeFromMealPlan(
                                    mealPlanDayId = mealOrNull.mealPlanDayId,
                                    mealPlanSpotId = mealOrNull.mealPlanSpotId
                                )
                            }
                        )
                    } else {
                        MealCard(
                            index = i,
                            onClickAdd = {
                                navController.navigate(Destination.SearchRecipe.route)
                                navController.setNavKey(NavKeys.Date, selectedDate.toString())
                                navController.setNavKey(NavKeys.Destination, Destination.MealPlanCalendar.route)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderRow(
    selectedDate: LocalDate,
    mealPlan: MealPlanDayWithSpots? = null
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(text = "${selectedDate.dayOfMonth.toDigits()}.${selectedDate.monthNumber.toDigits()}.${selectedDate.year}")
    }
}

@Composable
fun CalendarSlider(
    days: List<LocalDate>,
    selectedDate: LocalDate,
    isPlanedValue: List<Int> = emptyList(),
    shouldPlanedValue: Int,
    onClick: (LocalDate) -> Unit = {}
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(days) { it ->
            val isPlanedList: Int = try {
                isPlanedValue[days.indexOf(it)]
            } catch (e: Exception) {
                0
            }

            val ifDateIsNotTodayAndIsFirstOfMonth = it.dayOfMonth.toDigits() == "01" && days.first().dayOfMonth.toDigits() != "01"
            val isSelected = it == selectedDate
            val isToday = it == DateRepository.today

            CalendarDayCard(
                date = it,
                ifDateIsNotTodayAndIsFirstOfMonth = ifDateIsNotTodayAndIsFirstOfMonth,
                isSelected = isSelected,
                isToday = isToday,
                isPlanedValue = isPlanedList,
                shouldPlanedValue = shouldPlanedValue,
                onClick = {
                    onClick(it)
                }
            )
        }
    }
}

@Composable
fun CalendarDayCard(
    date: LocalDate,
    ifDateIsNotTodayAndIsFirstOfMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    isPlanedValue: Int = 0,
    shouldPlanedValue: Int = 0,
    onClick: (LocalDate) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(start = if (ifDateIsNotTodayAndIsFirstOfMonth) 20.dp else 0.dp)
            .size(70.dp)
            .sectionShadow()
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.medium
            )
            .clickableWithRipple {
                onClick(date)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HeadlineText(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "${date.dayOfMonth.toDigits()}.${date.monthNumber.toDigits()}",
            size = 16.sp,
            weight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
        FooterText(text = "$isPlanedValue / $shouldPlanedValue")
    }
}


@Composable
fun MealCard(
    index: Int = 1,
    meal: data.local.entitiy.Meal? = null,
    onClickAdd: () -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    val isExpanded = remember { mutableStateOf(false) }

    val expandAnimationAlpha by animateFloatAsState(
        targetValue = if (isExpanded.value) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
        ), label = "Infomation Animation"
    )

    Section{
        if(meal != null) {
            Column(
                modifier = Modifier
                    .onLongPress { onLongPress() }
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing // Type of easing
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HeadlineText(
                        maxLines = 1,
                        size = 16.sp,
                        text = meal.name
                    )

                    IconLabel(
                        modifier = Modifier.clickableWithRipple {
                            isExpanded.value = !isExpanded.value
                        },
                        icon = Res.drawable.ic_info
                    )
                }

                if(isExpanded.value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(expandAnimationAlpha),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconLabel(icon = Res.drawable.ic_fat, text = "${meal.fat}g")

                        IconLabel(icon = Res.drawable.ic_sugar, text = "${meal.sugar}g")

                        IconLabel(icon = Res.drawable.ic_protein, text = "${meal.protein}g")

                        IconLabel(icon = Res.drawable.ic_kcal,text = "${meal.kcal}kcal")
                    }
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconLabel(icon = Res.drawable.ic_add_timer, text = "${meal.preparationTimeInMinutes}min")

                    IconLabel(icon = if(meal.isPrivate) Res.drawable.ic_person_fill_view else Res.drawable.ic_person_fill_view, text = if(meal.isPrivate) "Privates Rezept" else "Öffentliches Rezept")
                }
            }
        } else {
            NoMeal(index) { onClickAdd() }
        }
    }
}

@Composable
fun RowScope.IconLabel(
    modifier: Modifier = Modifier,
    icon: DrawableResource,
    text: String = "",
) {
    this.apply {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = vectorResource(resource = icon),
                contentDescription = ""
            )
            BodyText(text = text)
        }
    }
}

@Composable
fun NoMeal(
    index: Int,
    onClick: () -> Unit = {}
){
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BodyText(
            modifier = Modifier,
            text = "${index}."
        )

        BodyText(
            modifier = Modifier.weight(1f),
            text = "Keine Mahlzeit zugewiesen"
        )

        IconButton(
            resource = Res.drawable.ic_plus
        ) {
            onClick()
        }
    }
}

@Composable
fun OverViewSection(
    operationTimestamp: Long = 0,
    mealCount: Int = 0,
    sugaredMealCount: Int,
    proteinMealCount: Int,
    fatMealCount: Int,
    kcalMealCount: Int
) {
    Section {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            val currentKcalLevel = calculateKcalLevelSafely(operationTimestamp)
            if (currentKcalLevel != null) {
                BodyText("Aktuelles Kcal-Level: ${currentKcalLevel.kcal}")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val proteinGoal = 80f
                val proteinProgress = proteinMealCount / proteinGoal
                ProgressComponent(
                    modifier = Modifier.weight(1f),
                    progress = proteinProgress.coerceIn(0f, 1f),
                    title = "Protein",
                    description = "$proteinMealCount / ${proteinGoal}g" // 40 / 80
                )

                Spacer(Modifier.weight(1f))


                val kcalGoal = 311f
                val kcalProgress = kcalMealCount / kcalGoal
                ProgressComponent(
                    modifier = Modifier.weight(1f),
                    progress = kcalProgress.coerceIn(0f, 1f),
                    title = "Kohlenhydrate",
                    description = "$kcalMealCount / 311g"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val sugaredGoal = (mealCount * 15).toFloat()
                val sugaredProgress = sugaredMealCount / sugaredGoal
                ProgressComponent(
                    modifier = Modifier.weight(1f),
                    progress = sugaredProgress.coerceIn(0f, 1f),
                    title = "Zucker",
                    description = "$sugaredMealCount / ${(mealCount * 15)}g"
                )

                Spacer(Modifier.weight(1f))

                val fatMealGoal = 40f
                val fatProgress = fatMealCount / fatMealGoal
                ProgressComponent(
                    modifier = Modifier.weight(1f),
                    progress = fatProgress.coerceIn(0f, 1f),
                    title = "Fett",
                    description = "$fatMealCount / ${fatMealGoal}g"
                )
            }
        }
    }
}

@Composable
fun ProgressComponent(
    modifier: Modifier = Modifier,
    title: String = "",
    description: String = "",
    progress: Float = 0f
) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BodyText(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = title
        )

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .border(
                    width = 0.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                ),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
            strokeCap = StrokeCap.Round
        )

        FooterText(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = description
        )
    }
}

fun calculateKcalLevelSafely(operationTimestamp: Long): KcalLevel? {
    val currentTimestamp: Long = Clock.System.now().toEpochMilliseconds()

    val operationInstant = try {
        Instant.fromEpochMilliseconds(operationTimestamp)
    } catch (e: IllegalArgumentException) {
        null
    } ?: return null

    val operationDate = operationInstant.toLocalDateTime(TimeZone.UTC).date
    val currentInstant = Instant.fromEpochMilliseconds(currentTimestamp)
    val currentDate = currentInstant.toLocalDateTime(TimeZone.UTC).date

    if (operationDate > currentDate) return null

    val monthsElapsed = (currentDate.year - operationDate.year) * 12 + (currentDate.monthNumber - operationDate.monthNumber)

    return when {
        monthsElapsed <= 3 -> KcalLevel.Junior
        monthsElapsed <= 6 -> KcalLevel.Mid
        else -> KcalLevel.SeniorKcalLevel
    }
}