package ui.screens.authScreens.recipeCategories

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_plus
import bauchglueck.composeapp.generated.resources.ic_search
import bauchglueck.composeapp.generated.resources.img_beilage
import bauchglueck.composeapp.generated.resources.img_dessert
import bauchglueck.composeapp.generated.resources.img_fueh_phase
import bauchglueck.composeapp.generated.resources.img_hauptgericht
import bauchglueck.composeapp.generated.resources.img_low_carb
import bauchglueck.composeapp.generated.resources.img_low_fat
import bauchglueck.composeapp.generated.resources.img_proteinreich
import bauchglueck.composeapp.generated.resources.img_puerierte_phase
import bauchglueck.composeapp.generated.resources.img_snack
import bauchglueck.composeapp.generated.resources.img_weiche_kost
import data.model.RecipeCategory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.lighthousegames.logging.logging
import ui.components.DatePickerOverLay
import ui.components.FormScreens.FormTextFieldWithIconAndDeleteButton
import ui.components.extentions.getSize
import ui.components.extentions.sectionShadow
import ui.components.theme.ScreenHolder
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import ui.navigations.NavKeys
import ui.navigations.setNavKey
import util.ApplicationContextHolder.context
import util.hideKeyboard
import viewModel.RecipeViewModel
import kotlin.math.ceil

@OptIn(FlowPreview::class)
fun NavGraphBuilder.recipeCategories(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel
) {
    composable(Destination.RecipeCategories.route) {
        var showDatePicker by remember { mutableStateOf(false) }

        val searchInput = remember { mutableStateOf(false) }
        val searchQuery by recipeViewModel.searchQuery.collectAsStateWithLifecycle()
        val foundRecipes by recipeViewModel.foundRecipes.collectAsStateWithLifecycle()
        val recipeForCategories by recipeViewModel.recipeForCategories.collectAsStateWithLifecycle()
        var searchJob: Job? = remember { null }

        val predicate =  !searchInput.value || searchQuery.isEmpty()

        val categories = recipeForCategories.flatMap { it.categories }
            .map { it.name }
            .distinct() // Remove duplicates
            .map { name ->
                name
            }

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

        ScreenHolder(
            title = Destination.RecipeCategories.title,
            showBackButton = true,
            onNavigate = {
                recipeViewModel.updateSearchQuery("")
                navController.navigate(Destination.Home.route)
            },
            optionsRow = {
                Icon(
                    imageVector = vectorResource(resource = Res.drawable.ic_plus),
                    contentDescription = "",
                    modifier = Modifier
                        .size(24.dp)
                        .clickableWithRipple {
                            navController.navigate(Destination.AddRecipe.route)
                            navController.setNavKey(
                                NavKeys.Destination,
                                Destination.AddRecipe.route
                            )
                        },
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = vectorResource(resource = Res.drawable.ic_search),
                        contentDescription = "",
                        modifier = Modifier
                            .size(24.dp)
                            .clickableWithRipple {
                                searchInput.value = !searchInput.value
                            },
                    )
                    Box(
                        modifier = Modifier
                            .width(15.dp)
                            .height(2.dp)
                            .background(if (searchInput.value) MaterialTheme.colorScheme.primary else Color.Transparent)
                    )
                }
            }
        ) {
            val size = remember { mutableStateOf(IntSize.Zero) }

            val itemsPerRow = if(predicate) 1 else 2
            val gap = 16.dp
            val cardSizePx = (size.value.width / itemsPerRow)
            val cardRows = ceil(
                if(predicate) {
                    recipeForCategories.size / itemsPerRow.toDouble()
                } else {
                    foundRecipes.size / itemsPerRow.toDouble()
                }
            ).toInt()

            // Umrechnung von Pixel in dp
            val cardSizeDp = with(LocalDensity.current) { cardSizePx.toDp() }
            val gridSizeDp = (cardSizeDp + gap) * cardRows


            if(searchInput.value) {
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
            }

            LazyVerticalGrid(
                modifier = Modifier
                    .height(gridSizeDp)
                    .getSize {
                        size.value = it
                    }
                    .fillMaxWidth(),
                columns = GridCells.Fixed(itemsPerRow),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {


                if(predicate) {
                    items(categories.size) {
                        val category = categories[it]
                        val image = when (RecipeCategory.fromStrong(category)) {
                            RecipeCategory.HAUPTGERICHT -> Res.drawable.img_hauptgericht
                            RecipeCategory.BEILAGE -> Res.drawable.img_beilage
                            RecipeCategory.DESSERT -> Res.drawable.img_dessert
                            RecipeCategory.FRUEH_PHASE -> Res.drawable.img_fueh_phase
                            RecipeCategory.PUERIERTE_PHASE -> Res.drawable.img_puerierte_phase
                            RecipeCategory.WEICHE_KOST -> Res.drawable.img_weiche_kost
                            RecipeCategory.NORMALE_KOST -> Res.drawable.img_beilage // TODO FEHLT
                            RecipeCategory.PROTEINREICH -> Res.drawable.img_proteinreich
                            RecipeCategory.LOW_FAT -> Res.drawable.img_low_fat
                            RecipeCategory.LOW_CARB -> Res.drawable.img_low_carb
                            RecipeCategory.SNACK -> Res.drawable.img_snack
                            null -> Res.drawable.img_beilage
                        }

                        Card(
                            modifier = Modifier.sectionShadow()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickableWithRipple {
                                        navController.navigate(Destination.RecipeList.route)
                                        navController.setNavKey(
                                            NavKeys.RecipeCategory,
                                            category.lowercase()
                                        )
                                        navController.setNavKey(
                                            NavKeys.Destination,
                                            Destination.RecipeCategories.route
                                        )
                                    },
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Image(
                                    modifier = Modifier.fillMaxWidth(),
                                    painter = painterResource(resource = image),
                                    contentDescription = "Bild $category",
                                    contentScale = ContentScale.FillWidth,
                                )
                                Row(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface.copy(0.95f))
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    HeadlineText(text = category, size = 16.sp)
                                }
                            }
                        }
                    }
                } else {
                    items(foundRecipes.size) {
                        ui.screens.authScreens.searchRecipes.Card(
                            recipe = foundRecipes[it],
                            onClickCard = {
                                recipeViewModel.setSelectedRecipe(foundRecipes[it])
                                navController.navigate(Destination.RecipeDetailScreen.route)
                            },
                            onClickIcon = {
                                recipeViewModel.setSelectedRecipe(foundRecipes[it])
                                showDatePicker = !showDatePicker
                            }
                        )
                    }
                }

            }

            if(predicate) {
                FooterText(text = "${recipeForCategories.size} Rezepte in ${categories.size} Kategorien")
            } else {
                FooterText(text = "${foundRecipes.size} Rezepte")
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
                        logging().info { "Date: $timeStamp" }
                    }
                }
            )
        }
    }
}