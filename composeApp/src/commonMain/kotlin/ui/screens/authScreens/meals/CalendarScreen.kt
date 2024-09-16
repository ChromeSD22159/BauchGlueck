package ui.screens.authScreens.meals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ui.components.BackScaffold
import ui.components.clickableWithRipple
import ui.navigations.Destination
import ui.theme.AppTheme
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel
import util.DateRepository
import viewModel.FirebaseAuthViewModel
import viewModel.MealViewModel

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    navController: NavController,
    backNavigationDirection: Destination = Destination.Home,
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    val viewModel = viewModel<MealViewModel>()
    val userFormState by firebaseAuthViewModel.userFormState.collectAsStateWithLifecycle(initialValue = null)
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle(initialValue = DateRepository.today)

    BackScaffold(
        title = Destination.Calendar.title,
        backNavigationDirection = backNavigationDirection,
        navController = navController
    ) {
        Calendar(days = viewModel.days, selectedDate) { viewModel.setSelectedDate(it) }

        userFormState?.userProfile?.value?.totalMeals?.let { it ->
            DateCard(date = selectedDate)

            for (i in 1..it) {
                MealCard(count = i)
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
        items(days) {
            Box(
                Modifier
                    .background(
                        color = if (it == selectedDate) Color.Gray.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.9f),
                        shape = CircleShape
                    )
                    .border(
                        width = if (it == DateRepository.today) 2.dp else 0.dp,
                        color = if (it == DateRepository.today) Color.Black.copy(alpha = 0.7f) else Color.Gray.copy(alpha = 0f),
                        shape = CircleShape
                    )
                    .size(25.dp)
                    .clickableWithRipple {
                        onClick(it)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    color = Color.White,
                    text = it.dayOfMonth.toDigits()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MealCardPreview() {
    AppTheme {
        DateCard(DateRepository.today)
    }
}

@Composable
fun MealCard(
    count: Int = 1
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Gray.copy(alpha = 0.7f), shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${count}. Slot")
    }
}

@Composable
fun DateCard(
    date: LocalDate,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.Gray.copy(alpha = 0.7f), shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${date.dayOfMonth.toDigits()}.${date.monthNumber.toDigits()}.${date.year}")
    }
}

fun Int.toDigits(): String {
    return this.toString().padStart(2, '0')
}