package ui.screens.authScreens.searchRecipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_calendar
import bauchglueck.composeapp.generated.resources.ic_fat
import bauchglueck.composeapp.generated.resources.ic_protein
import bauchglueck.composeapp.generated.resources.ic_search
import bauchglueck.composeapp.generated.resources.placeholder_image
import coil3.compose.AsyncImage
import data.local.entitiy.MealWithCategories
import di.serverHost
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.lighthousegames.logging.logging
import ui.components.FormScreens.FormTextFieldWithIconAndDeleteButton
import ui.components.IconListWithText
import ui.components.extentions.getSize
import ui.components.theme.ScreenHolder
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.navigations.Destination
import ui.navigations.NavKeys
import ui.navigations.setNavKey
import ui.screens.authScreens.recipeCategories.DatePickerOverLay
import util.hideKeyboard
import util.parseToLocalDate
import viewModel.RecipeViewModel
import kotlin.math.ceil

@OptIn(FlowPreview::class)
fun NavGraphBuilder.searchRecipes(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel
) {
    composable(Destination.SearchRecipe.route) {  backStackEntry ->
        val context = LocalContext.current

        val selectedDate = backStackEntry.savedStateHandle.get<String>(NavKeys.Date.key)?.parseToLocalDate()
        val destination = backStackEntry.savedStateHandle.get<String>("destination")

        var showDatePicker by remember { mutableStateOf(false) }

        val searchQuery by recipeViewModel.searchQuery.collectAsStateWithLifecycle()
        val recipes by recipeViewModel.foundRecipes.collectAsStateWithLifecycle()
        var searchJob: Job? = remember { null }

        LaunchedEffect(Unit) {
            snapshotFlow { searchQuery }
                .debounce(200)
                .collect { query ->
                    if (query.isNotEmpty()) {
                        searchJob?.cancel()
                        searchJob = recipeViewModel.scope.launch {
                            recipeViewModel.updateSearchQuery(query)
                        }
                    }
                }
        }

        val size = remember { mutableStateOf(IntSize.Zero) }

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

            FormTextFieldWithIconAndDeleteButton(
                icon = Res.drawable.ic_search,
                inputValue = searchQuery,
                onValueChange = {
                    recipeViewModel.updateSearchQuery(it)
                },
                onClickAction = {
                    hideKeyboard(context)
                    recipeViewModel.updateSearchQuery("")
                }
            )

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
                            if(selectedDate == null) {
                                // SELECTED DATE FROM NAVSTACK
                                recipeViewModel.setSelectedRecipe(recipes[it])
                                showDatePicker = true
                            } else {
                                // NONE SELECTED DATE FROM NAVSTACK
                                navController.navigate(Destination.MealPlanCalendar.route)
                                navController.setNavKey(NavKeys.Date, selectedDate.toString())
                                navController.setNavKey(NavKeys.RecipeId, recipes[it].meal.mealId)
                            }
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

        // WHEN NONE DATE IS GIVEN FROM NAVSTACK AND ADD
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
                    logging().info { "Date: $timeStamp" }
                }
            }
        )
    }
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    recipe: MealWithCategories,
    onClickIcon: () -> Unit = {},
    onClickCard: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
    ) {
        // Background image
        if (recipe.meal.mainImage?.formats?.small?.url != null) {
            AsyncImage(
                model = serverHost + recipe.meal.mainImage?.formats?.small?.url,
                placeholder = painterResource(Res.drawable.placeholder_image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                bitmap = imageResource(resource = Res.drawable.placeholder_image),
                contentDescription = "placeholder",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }


        // Content area overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f)
                        )
                    )
                ),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(
                modifier = Modifier
                    .clickableWithRipple {
                        onClickCard()
                    }
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .padding(8.dp),
            ) {
                BodyText(
                    text = recipe.meal.name,
                    maxLines = 1,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconListWithText(
                        rowModifier = Modifier.weight(1f),
                        items = listOf(
                            Pair(Res.drawable.ic_protein, "${recipe.meal.protein}g"),
                            Pair(Res.drawable.ic_fat, "${recipe.meal.fat}g")
                        )
                    )
                }
            }
        }

        // Icon in top right corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_add_calendar),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp) // Adjust size to increase clickable area
                    .clickableWithRipple {
                        onClickIcon()
                    },
            )
        }
    }
}

data class ApiResponse(
    val date: String = "",
    val time: String = "",
    val listPerson: List<Person>
)

data class Person(
    val vorname: String = "Unknown Firstname",
    val nachname: String = "Unknown Lastname",
    val alter: Int = 0
)