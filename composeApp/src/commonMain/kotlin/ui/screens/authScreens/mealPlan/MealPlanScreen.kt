package ui.screens.authScreens.mealPlan

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_gear
import bauchglueck.composeapp.generated.resources.ic_plus
import bauchglueck.composeapp.generated.resources.ic_seal_check
import bauchglueck.composeapp.generated.resources.ic_seal_xmark
import bauchglueck.composeapp.generated.resources.ic_search
import data.remote.model.Meal
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.ScreenHolder
import ui.components.theme.Section
import ui.components.theme.clickableWithRipple
import ui.components.theme.sectionShadow
import ui.components.theme.text.BodyText
import ui.components.theme.toDigits
import ui.navigations.Destination
import util.DateRepository
import viewModel.FirebaseAuthViewModel
import viewModel.MealViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mealPlan(
    navController: NavHostController,
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    composable(Destination.MealPlanCalendar.route) {
        val viewModel = viewModel<MealViewModel>()
        val userFormState by firebaseAuthViewModel.userFormState.collectAsStateWithLifecycle(initialValue = null)
        val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle(initialValue = DateRepository.today)

        ScreenHolder(
            title = Destination.MealPlanCalendar.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.Home.route)
            },
            optionsRow = {
                Icon(
                    imageVector = vectorResource(resource = Res.drawable.ic_plus),
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp)
                        .clickableWithRipple { navController.navigate(Destination.AddRecipe.route) },
                )

                Icon(
                    imageVector = vectorResource(resource = Res.drawable.ic_search),
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp)
                        .clickableWithRipple { navController.navigate(Destination.SearchRecipe.route) },
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

            Text(text = "${selectedDate.dayOfMonth.toDigits()}.${selectedDate.monthNumber.toDigits()}.${selectedDate.year}")

            Calendar(days = viewModel.days, selectedDate) { viewModel.setSelectedDate(it) }

            userFormState?.userProfile?.value?.totalMeals?.let {
                for (i in 1..it) {
                    MealCard(index = i)
                }
            }
        }
    }
}

@Composable
fun Calendar(
    days: List<LocalDate>,
    selectedDate: LocalDate,
    onClick: (LocalDate) -> Unit = {}
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(days) { it ->
            val ifDateIsNotTodayAndIsFirstOfMonth = it.dayOfMonth.toDigits() == "01" && days.first().dayOfMonth.toDigits() != "01"
            val isSelected = it == selectedDate
            val isToday = it == DateRepository.today

            val selectedBrush = Brush.verticalGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.onPrimary
                )
            )

            val notSelectedBrush = Brush.verticalGradient(
                listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                )
            )

            CalendarDayCard(
                date = it,
                ifDateIsNotTodayAndIsFirstOfMonth = ifDateIsNotTodayAndIsFirstOfMonth,
                isSelected = isSelected,
                isToday = isToday,
                selectedBrush = selectedBrush,
                notSelectedBrush = notSelectedBrush,
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
    selectedBrush: Brush,
    notSelectedBrush: Brush,
    isDayisTotalyPlaned: Boolean = false,
    onClick: (LocalDate) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(start = if (ifDateIsNotTodayAndIsFirstOfMonth) 20.dp else 0.dp)
            .size(60.dp)
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
        BodyText(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = date.dayOfMonth.toDigits(),
            weight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
        Icon(
            imageVector = vectorResource(resource = if (isDayisTotalyPlaned) Res.drawable.ic_seal_check else Res.drawable.ic_seal_xmark),
            contentDescription = "",
            modifier = Modifier.size(20.dp).alpha(if (isDayisTotalyPlaned) 1f else 0.5f),
            tint = if (isDayisTotalyPlaned) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}


@Composable
fun MealCard(
    index: Int = 1,
    meal: Meal? = null
) {
    Section{
        if(meal != null) {

        } else {
            NoMeal(index)
        }
    }
}

@Composable
fun NoMeal(
    index: Int,
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
    }
}