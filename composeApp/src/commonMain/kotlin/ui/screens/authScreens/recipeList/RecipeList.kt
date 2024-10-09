package ui.screens.authScreens.recipeList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.lighthousegames.logging.logging
import ui.components.DatePickerOverLay
import ui.components.extentions.getSize
import ui.components.theme.ScreenHolder
import ui.components.theme.text.FooterText
import ui.navigations.Destination
import ui.navigations.NavKeys
import ui.navigations.setNavKey
import ui.screens.authScreens.searchRecipes.Card
import viewModel.RecipeViewModel
import kotlin.math.ceil

fun NavGraphBuilder.recipesList(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel
) {
    composable(Destination.RecipeList.route) { backStackEntry ->
        val selectedCategory = backStackEntry.savedStateHandle.get<String>(NavKeys.RecipeCategory.key)
        val destination = backStackEntry.savedStateHandle.get<String>("destination")

        logging().info { "Selected category: $selectedCategory" }

        val recipesList by recipeViewModel.foundRecipes.collectAsStateWithLifecycle()
        val recipes = recipesList.filter { it.meal.categoryId?.lowercase() == selectedCategory }

        val size = remember { mutableStateOf(IntSize.Zero) }

        var showDatePicker by remember { mutableStateOf(false) }

        val itemsPerRow = 2
        val gap = 16.dp
        val cardSizePx = (size.value.width / itemsPerRow)
        val cardRows = ceil(recipes.size / itemsPerRow.toDouble()).toInt()

        // Umrechnung von Pixel in dp
        val cardSizeDp = with(LocalDensity.current) { cardSizePx.toDp() }
        val gridSizeDp = (cardSizeDp + gap) * cardRows

        ScreenHolder(
            title = Destination.SearchRecipe.title,
            showBackButton = true,
            onNavigate = {
                recipeViewModel.clearSearchQuery()
                destination?.let { destination -> navController.navigate(destination) }
            },
            optionsRow = {}
        ) {

            LazyVerticalGrid(
                modifier = Modifier
                    .getSize {
                        size.value = it
                    }
                    .height(gridSizeDp)
                    .fillMaxWidth(),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(recipes.size) {
                    Card(
                        recipe = recipes[it],
                        onClickCard = {
                            recipeViewModel.setSelectedRecipe(recipes[it])
                            navController.navigate(Destination.RecipeDetailScreen.route)
                        },
                        onClickIcon = {
                            recipeViewModel.setSelectedRecipe(recipes[it])
                            showDatePicker = !showDatePicker
                        }
                    )
                }
            }

            FooterText(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "${recipes.size} Ergebnisse gefunden}"
            )
        }

        DatePickerOverLay(
            showDatePicker,
            onDatePickerStateChange = { showDatePicker = it },
            onConformDate = { timeStamp ->
                recipeViewModel.selectedRecipe.value?.meal?.mealId?.let { mealId ->
                    navController.navigate(Destination.MealPlanCalendar.route)
                    navController.setNavKey(NavKeys.RecipeId, mealId)
                    val localDate = timeStamp?.let { it1 ->
                        Instant.fromEpochMilliseconds(it1)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                    }
                    navController.setNavKey(NavKeys.Date, localDate.toString())
                }
            }
        )
    }
}