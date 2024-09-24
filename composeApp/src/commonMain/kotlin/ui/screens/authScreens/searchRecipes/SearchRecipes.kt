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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
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
import data.remote.model.ApiRecipesResponse
import di.serverHost
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import ui.components.FormScreens.FormTextFieldWithIconAndDeleteButton
import ui.components.IconListWithText
import ui.components.theme.ScreenHolder
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.navigations.Destination
import viewModel.RecipeViewModel
import kotlin.math.ceil

@OptIn(FlowPreview::class)
fun NavGraphBuilder.searchRecipes(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel
) {
    composable(Destination.SearchRecipe.route) {
        val searchQuery by recipeViewModel.searchQuery.collectAsStateWithLifecycle()
        val recipes by recipeViewModel.recipes.collectAsStateWithLifecycle()
        var searchJob: Job? = remember { null }

        LaunchedEffect(Unit) {
            snapshotFlow { searchQuery }
                .debounce(500) // Verwende eine Debounce-Zeit von 500ms
                .collect { query ->
                    // Überprüfe, ob der Suchbegriff nicht leer ist
                    if (query.isNotEmpty()) {
                        searchJob?.cancel() // Storniere den vorherigen Job, falls vorhanden
                        searchJob = recipeViewModel.scope.launch {
                            recipeViewModel.searchRecipes(query) // Führe die Suche durch
                        }
                    }
                }
        }

        val size = remember { mutableStateOf(IntSize.Zero) }

        // In deinem composable Funktionskörper
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
                navController.navigate(Destination.MealPlanCalendar.route)
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
                    recipeViewModel.resetRecipeList()
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
                        meal = recipes[it],
                        onClickCard = {
                            recipeViewModel.setSelectedRecipe(recipes[it])
                            navController.navigate(Destination.RecipeDetailScreen.route)
                        },
                        onClickIcon = {}
                    )
                }
            }

            FooterText(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "${recipes.size} Ergebnisse gefunden}"
            )
        }
    }
}

@Composable
fun Card(
    meal: ApiRecipesResponse,
    onClickIcon: () -> Unit = {},
    onClickCard: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
    ) {
        // Background image
        if (meal.mainImage?.formats?.small?.url != null) {
            AsyncImage(
                model = serverHost + meal.mainImage?.formats?.small?.url,
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
                )
                .clickableWithRipple {
                    onClickCard()
                },
            contentAlignment = Alignment.BottomStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .padding(8.dp),
            ) {
                BodyText(
                    text = meal.name,
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
                            Pair(Res.drawable.ic_protein, "${meal.protein}g"),
                            Pair(Res.drawable.ic_fat, "${meal.fat}g")
                        )
                    )
                }
            }
        }

        // Icon in top right corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_add_calendar),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickableWithRipple {
                        onClickIcon()
                    }
            )
        }
    }
}

fun Modifier.getSize(size: (IntSize) -> Unit): Modifier {
    return this.onGloballyPositioned { coordinates ->
        size(coordinates.size)
    }
}