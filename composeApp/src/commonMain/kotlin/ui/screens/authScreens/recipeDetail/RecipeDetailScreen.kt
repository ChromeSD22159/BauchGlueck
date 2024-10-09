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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_calendar
import bauchglueck.composeapp.generated.resources.ic_clock
import bauchglueck.composeapp.generated.resources.ic_fat
import bauchglueck.composeapp.generated.resources.ic_kcal
import bauchglueck.composeapp.generated.resources.ic_protein
import bauchglueck.composeapp.generated.resources.placeholder_image
import coil3.compose.AsyncImage
import data.local.entitiy.MealWithCategories
import di.serverHost
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import ui.components.DatePickerOverLay
import ui.components.theme.AppBackground
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import ui.navigations.NavKeys
import ui.navigations.setNavKey
import ui.theme.AppTheme
import viewModel.RecipeViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.recipeDetails(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel,
) {
    composable(
        Destination.RecipeDetailScreen.route,
    ) {
        val recipeOrNull by recipeViewModel.selectedRecipe.collectAsStateWithLifecycle()

        recipeOrNull?.let { recipe ->
            Recipe(
                recipe,
                navController = navController,
                onClose = {
                    recipeViewModel.clearSelectedRecipe()
                    navController.navigate(Destination.SearchRecipe.route)
                },
                recipeViewModel = recipeViewModel
            )
        }
    }
}

@Composable
fun Recipe(
    recipe: MealWithCategories,
    navController: NavHostController,
    share: Dp = 40.dp,
    recipeViewModel: RecipeViewModel,
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

    var showDatePicker by remember { mutableStateOf(false) }

    AppTheme {
        AppBackground {
            Box {
                // Hintergrundbild mit Parallax-Effekt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    if(recipe.meal.mainImage?.formats?.medium?.url != null) {
                        AsyncImage(
                            model = serverHost + recipe.meal.mainImage?.formats?.medium?.url,
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

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.4f),
                                    )
                                )
                            )
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
                            text = recipe.meal.name,
                            size = 16.sp,
                        )

                        // Beispielhafte Nutrition Icons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            NutrinIcon(
                                icon = Res.drawable.ic_kcal,
                                text = "${recipe.meal.kcal.toInt()}g",
                                rowScope = this
                            )

                            NutrinIcon(
                                icon = Res.drawable.ic_protein,
                                text = "${recipe.meal.protein.toInt()}g",
                                rowScope = this
                            )

                            NutrinIcon(
                                icon = Res.drawable.ic_fat,
                                text = "${recipe.meal.fat.toInt()}g",
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
                                FooterText( "${recipe.meal.preparationTimeInMinutes} Minuten" )
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
                                FooterText(recipe.categories.first().name)
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HeadlineText("Beschreibung:", size = 14.sp)
                            BodyText(recipe.meal.description)
                        }

                        // Zutatenliste
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HeadlineText("Zutatenliste:", size = 14.sp)
                            recipe.meal.ingredients.forEach {
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
                        }

                        // Rezeptbeschreibung
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HeadlineText("Zubereitung:", size = 14.sp)
                            BodyText(recipe.meal.preparation)
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
                            recipeViewModel.setSelectedRecipe(recipe)
                            showDatePicker = !showDatePicker
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

            DatePickerOverLay(
                showDatePicker,
                onDatePickerStateChange = { showDatePicker = it },
                onConformDate = { timeStamp ->
                    navController.navigate(Destination.MealPlanCalendar.route)
                    navController.setNavKey(NavKeys.RecipeId, recipe.meal.mealId)
                    val localDate = timeStamp?.let { it1 ->
                        Instant.fromEpochMilliseconds(it1)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .date
                    }
                    navController.setNavKey(NavKeys.Date, localDate.toString())
                }
            )
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