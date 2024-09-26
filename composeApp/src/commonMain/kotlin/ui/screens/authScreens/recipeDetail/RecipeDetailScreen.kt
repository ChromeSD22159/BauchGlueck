package ui.screens.authScreens.recipeDetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_calendar
import bauchglueck.composeapp.generated.resources.ic_add_timer
import bauchglueck.composeapp.generated.resources.ic_clock
import bauchglueck.composeapp.generated.resources.ic_fat
import bauchglueck.composeapp.generated.resources.ic_kcal
import bauchglueck.composeapp.generated.resources.ic_protein
import bauchglueck.composeapp.generated.resources.ic_sugar
import bauchglueck.composeapp.generated.resources.icon_calendar
import bauchglueck.composeapp.generated.resources.placeholder_image
import coil3.compose.AsyncImage
import data.remote.model.ApiRecipesResponse
import data.remote.model.Category
import data.remote.model.Ingredient
import de.frederikkohler.bauchglueck.R
import di.serverHost
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Resource
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.lighthousegames.logging.logging
import ui.components.theme.AppBackground
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import ui.theme.AppTheme
import util.debugJsonHelper
import viewModel.RecipeViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.recipeDetails(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel
) {
    composable(
        Destination.RecipeDetailScreen.route,
    ) {
        val recipeOrNull by recipeViewModel.selectedRecipe.collectAsStateWithLifecycle()

        recipeOrNull?.let { recipe ->
            Recipe(
                recipe,
                onClose = {
                    recipeViewModel.clearSelectedRecipe()
                    navController.navigate(Destination.SearchRecipe.route)
                },
                onAddToMealPlan = {

                }
            )
        }
    }
}

@Composable
fun Recipe(
    meal: ApiRecipesResponse,
    share: Dp = 40.dp,
    onAddToMealPlan: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    // Definiere den ScrollState
    val scrollState = rememberScrollState()

    val alphaControlButtons by animateFloatAsState(
        targetValue = if (scrollState.value in 400..550) {
            1f - ((scrollState.value - 400) / 150f)
        } else if (scrollState.value > 550) {
            0f
        } else {
            1f
        },
        label = "Animate Control Button Opacity"
    )

    val alphaImage by animateFloatAsState(
        targetValue = if (scrollState.value in 400..600) {
            1f - ((scrollState.value - 400) / 150f)
        } else if (scrollState.value > 600) {
            0f
        } else {
            1f
        },
        label = "Animate Control Button Opacity"
    )

    AppTheme {
        AppBackground {
            Box {
                // Hintergrundbild mit Parallax-Effekt
                if(meal.mainImage?.formats?.medium?.url != null) {
                    AsyncImage(
                        model = serverHost + meal.mainImage?.formats?.medium?.url,
                        placeholder = painterResource(Res.drawable.placeholder_image),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .alpha(alphaImage)
                            .graphicsLayer {
                                // Parallax-Effekt basierend auf der Scrollposition
                                scaleX = 1 + (scrollState.value / 5000f)
                                scaleY = 1 + (scrollState.value / 5000f)
                            }
                    )
                } else {
                    Image(
                        painter = painterResource(Res.drawable.placeholder_image), // Platzhalterbild
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .graphicsLayer {
                                // Parallax-Effekt basierend auf der Scrollposition
                                translationY = 0.5f * scrollState.value
                                scaleX = 1 + (scrollState.value / 5000f)
                                scaleY = 1 + (scrollState.value / 5000f)
                            }
                    )
                }

                // Overlay mit abgerundeten Ecken, das nach oben fährt
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState) // ScrollState hier verwendet
                ) {
                    Spacer(modifier = Modifier.height(250.dp)) // Platzhalter, damit das Bild vollständig sichtbar ist

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.background
                                    )
                                ),
                                shape = RoundedCornerShape(topStart = share, topEnd = share)
                            )
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Restlicher Inhalt des Overlays
                        HeadlineText(
                            text = meal.name,
                            size = 16.sp,
                        )

                        // Beispielhafte Nutrition Icons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            NutrinIcon(
                                icon = Res.drawable.ic_kcal,
                                text = "${meal.kcal.toInt()}g",
                                rowScope = this
                            )

                            NutrinIcon(
                                icon = Res.drawable.ic_protein,
                                text = "${meal.protein.toInt()}g",
                                rowScope = this
                            )

                            NutrinIcon(
                                icon = Res.drawable.ic_fat,
                                text = "${meal.fat.toInt()}g",
                                rowScope = this
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
                            ) {
                                Icon(
                                    imageVector = vectorResource(resource = Res.drawable.ic_clock),
                                    contentDescription = "share",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                FooterText( "${meal.preparationTimeInMinutes} Minuten" )
                            }

                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                            ) {
                                Icon(
                                    imageVector = vectorResource(resource = Res.drawable.ic_add_calendar),
                                    contentDescription = "share",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                FooterText(meal.category.name)
                            }
                        }

                        Column {
                            BodyText(meal.description)
                        }

                        // Zutatenliste
                        meal.ingredients.forEach {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color.White.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(50.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                BodyText("${it.amount} ${it.unit}")
                                BodyText(it.name)
                            }
                        }

                        // Rezeptbeschreibung
                        Column {
                            BodyText(meal.preparation)
                        }

                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }

                // ICON ITEMS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 42.dp, horizontal = 16.dp)
                        .alpha(alphaControlButtons),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
                ) {
                    Icon(
                        modifier = Modifier.clickableWithRipple {
                            onAddToMealPlan()
                        },
                        imageVector = vectorResource(resource = Res.drawable.ic_add_calendar),
                        contentDescription = "AddToMealPlan",
                        tint = Color.White
                    )

                    Icon(
                        modifier = Modifier.clickableWithRipple {
                            onClose()
                        },
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun NutrinIcon(
    icon: DrawableResource,
    rowScope: RowScope,
    text: String = "",
) {
    rowScope.apply {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = vectorResource(resource = icon),
                contentDescription = "share",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text)
        }
    }
}