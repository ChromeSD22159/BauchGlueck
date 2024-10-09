package ui.screens.authScreens.shoppingList

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.bubble_right_dark
import bauchglueck.composeapp.generated.resources.bubble_right_light
import bauchglueck.composeapp.generated.resources.ic_arrow_down
import bauchglueck.composeapp.generated.resources.ic_arrow_up
import bauchglueck.composeapp.generated.resources.ic_ellipsis
import bauchglueck.composeapp.generated.resources.ic_gear
import bauchglueck.composeapp.generated.resources.ic_plus
import bauchglueck.composeapp.generated.resources.ic_seal_check
import bauchglueck.composeapp.generated.resources.ic_trash
import bauchglueck.composeapp.generated.resources.magen
import data.local.entitiy.ShoppingList
import data.model.GenerateShoppingListState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import ui.components.DateRangePickerOverLay
import ui.components.extentions.sectionShadow
import ui.components.theme.ScreenHolder
import ui.components.theme.Section
import ui.components.theme.button.IconButton
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import ui.navigations.NavKeys
import ui.navigations.setNavKey
import ui.screens.authScreens.addRecipe.components.IconErrorRow
import ui.screens.authScreens.addRecipe.components.IconRow
import viewModel.ShoppingListViewModel

fun NavGraphBuilder.shoppingLists(
    navController: NavHostController
) {
    composable(Destination.ShoppingLists.route) { backStackEntry ->
        val viewModel: ShoppingListViewModel = viewModel<ShoppingListViewModel>()
        val showDatePicker by viewModel.showDatePicker.collectAsState()
        val shoppingLists by viewModel.shoppingLists.collectAsState()

        val sortDirection = remember {
            mutableStateOf(Sorting.ASC)
        }

        val sortedShoppingList: List<ShoppingList> = if(sortDirection.value == Sorting.ASC) {
            shoppingLists.sortedBy { it.updatedAtOnDevice }
        } else {
            shoppingLists.sortedByDescending { it.updatedAtOnDevice }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenHolder(
                title = Destination.ShoppingLists.title,
                showBackButton = true,
                onNavigate = {
                    navController.navigate(Destination.Home.route)
                },
                optionsRow = {
                    IconButton(
                        resource = Res.drawable.ic_gear,
                        tint = MaterialTheme.colorScheme.onPrimary
                    ) {
                        navController.navigate(Destination.Home.route)
                    }
                },
                pageSpacing = 0.dp,
                itemSpacing = 12.dp
            ) {

                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .sectionShadow()
                ) {
                    Box(
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .height(150.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Image(
                                painter = painterResource(if (isSystemInDarkTheme()) Res.drawable.bubble_right_dark else Res.drawable.bubble_right_light),
                                contentDescription = "bubble_right_light",
                                contentScale = ContentScale.FillHeight,
                            )
                        }

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .clickableWithRipple { viewModel.toggleDatePicker() },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Image(
                                    modifier = Modifier.size(80.dp),
                                    painter = painterResource(Res.drawable.magen),
                                    contentDescription = "magen",
                                    contentScale = ContentScale.FillHeight,
                                )
                            }

                            
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(2f),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.End
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FooterText(
                                        modifier = Modifier.clickableWithRipple { viewModel.toggleDatePicker() },
                                        text = "Einkaufsliste erstellen"
                                    )

                                    Icon(
                                        modifier = Modifier
                                            .background(Color.White.copy(0.2f), shape = CircleShape)
                                            .padding(2.dp),
                                        imageVector = vectorResource(resource = Res.drawable.ic_plus),
                                        contentDescription = ""
                                    )
                                }
                            }
                        }
                    }
                }

                if (shoppingLists.isEmpty()) {
                    NoShoppingList(
                        "Erstelle deine erste Einkaufsliste basierend auf deinen Mealplan!"
                    ) {
                        viewModel.toggleDatePicker()
                    }
                } else {
                    SortableButton(
                        modifier = Modifier.padding(top = 5.dp),
                        sortDirection.value
                    ) {
                        sortDirection.value = it
                    }

                    sortedShoppingList.forEach { shoppingList ->
                        ShoppingListItem(
                            shoppingList,
                            navController =  navController,
                            onComplete = {
                                viewModel.markListAsComplete(shoppingList)
                            },
                            onInComplete = {
                                viewModel.markListAsInComplete(shoppingList)
                            },
                            onDelete = {
                                viewModel.softDeleteShoppingList(shoppingList)
                            }
                        )
                    }

                    InfomationCard()
                }
            }

            GenerateShoppingListOverlay(
                viewModel
            )
        }

        DateRangePickerOverLay(
            showDatePicker,
            onDatePickerStateChange = { viewModel.toggleDatePicker() },
            onConformDate = { start, end->
                if(start != null && end != null) {
                    viewModel.startGenerateShoppingList(start, end)
                }
            }
        )
    }
}

@Composable
fun NoShoppingList(
    text: String,
    onClick: () -> Unit = {}
) {
    Section(
        sectionModifier = Modifier
            .clickableWithRipple { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                modifier = Modifier
                    .background(Color.White.copy(0.2f), shape = CircleShape)
                    .padding(2.dp),
                imageVector = vectorResource(resource = Res.drawable.ic_plus),
                contentDescription = ""
            )

            BodyText(
                modifier = Modifier,
                textAlign = TextAlign.Center,
                text = text
            )
        }
    }
}

@Composable
fun SortableButton(
    modifier: Modifier = Modifier,
    sortDirection: Sorting,
    onChangeSorting: (Sorting) -> Unit = {}
) {
    Row(
        modifier = modifier
            .clickableWithRipple {
                when (sortDirection) {
                    Sorting.ASC -> onChangeSorting(Sorting.DESC)
                    Sorting.DESC -> onChangeSorting(Sorting.ASC)
                }
            }
            .padding(horizontal = 10.dp)
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = vectorResource(resource = sortDirection.icon),
            contentDescription = ""
        )

        FooterText(
            size = 14.sp,
            text = "Sortierung"
        )
    }
}

@Composable
fun ShoppingListItem(
    shoppingList: ShoppingList,
    navController: NavHostController,
    onComplete: () -> Unit = {},
    onInComplete: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    Section(sectionModifier = Modifier
        .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .clickableWithRipple {
                    navController.navigate(Destination.ShoppingListDetail.route)
                    navController.setNavKey(NavKeys.RecipeId, shoppingList.shoppingListId)
                    navController.setNavKey(NavKeys.Destination, Destination.ShoppingLists.route)
                }
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyText(
                modifier = Modifier,
                text = "${shoppingList.startDate} - ${shoppingList.endDate}"
            )

            Icon(
                modifier = Modifier
                    .clickableWithRipple { expanded = true }
                    .background(Color.White.copy(0.2f), shape = CircleShape)
                    .padding(2.dp),
                imageVector = vectorResource(resource = if(shoppingList.isComplete) Res.drawable.ic_seal_check else Res.drawable.ic_seal_check),
                contentDescription = "",
                tint = Color.White.copy(if(shoppingList.isComplete) 1.0f else 0.5f)
            )

            Box {
                Icon(
                    modifier = Modifier
                        .clickableWithRipple { expanded = true }
                        .background(Color.White.copy(0.2f), shape = CircleShape)
                        .padding(2.dp),
                    imageVector = vectorResource(resource = Res.drawable.ic_ellipsis),
                    contentDescription = ""
                )

                DropdownMenu(
                    shape = RoundedCornerShape(8.dp),
                    expanded = expanded,
                    containerColor = MaterialTheme.colorScheme.background,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if(shoppingList.isComplete) "Nicht Erledigt" else "Erledigt") },
                        onClick = {
                            expanded = false
                            if(shoppingList.isComplete) onInComplete() else onComplete()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = vectorResource(resource = Res.drawable.ic_seal_check),
                                contentDescription = "Leading icon for plus",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Löschen") },
                        onClick = {
                            expanded = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = vectorResource(resource = Res.drawable.ic_trash),
                                contentDescription = "Leading icon for plus",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GenerateShoppingListOverlay(
    viewModel: ShoppingListViewModel
) {
    val inProgress = viewModel.inProgress.collectAsState()
    val isAnimating = viewModel.isAnimating.collectAsState()

    val overlayBackgroundAlpha by animateFloatAsState(
        targetValue = if (isAnimating.value) 0.5f else 0f,
        animationSpec = tween(
            durationMillis = 350,
            easing = LinearOutSlowInEasing
        ),
        label = "Overlay Alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = overlayBackgroundAlpha))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val contentAlpha by animateFloatAsState(
            targetValue = if (isAnimating.value) 1f else 0f,
            animationSpec = tween(
                durationMillis = 350,
                easing = LinearOutSlowInEasing
            ),
            label = "Overlay Content Alpha"
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .alpha(contentAlpha)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            when (inProgress.value) {
                GenerateShoppingListState.AnalyseMealPlans -> {
                    IconRow(isActive = true, text = "Analysiere MealPlans")
                    IconRow(isDone = false, text = "Zutatenliste berechnen")
                    IconRow(isDone = false, text = "Specherung der Einkaufliste")
                    IconErrorRow(isError = false, text = "Error")
                }
                GenerateShoppingListState.Calculate -> {
                    IconRow(isDone = true, text = "Analysiere MealPlans")
                    IconRow(isActive = true, text = "Zutatenliste berechnen")
                    IconRow(isDone = false, text = "Hochladen des Rezepts.")
                    IconErrorRow(isError = false, text = "Error")
                }
                GenerateShoppingListState.Done -> {
                    IconRow(isDone = true, text = "Analysiere MealPlans")
                    IconRow(isDone = true, text = "Zutatenliste berechnen")
                    IconRow(isActive = true, text = "Hochladen des Rezepts.")
                    IconErrorRow(isError = false, text = "Error")
                }
                else -> {
                    IconRow(text = "Analysiere MealPlans")
                    IconRow(text = "Zutatenliste berechnen")
                    IconRow(text = "Hochladen des Rezepts.")
                    IconErrorRow(isError = false, text = "Error")
                }
            }
        }
    }
}

enum class Sorting(val icon: DrawableResource) {
    ASC(Res.drawable.ic_arrow_up),
    DESC(Res.drawable.ic_arrow_down)
}

@Composable
fun InfomationCard() {
    Section(
        sectionModifier = Modifier.padding(horizontal = 10.dp)
    ) {
        FooterText(text = "🔔 Wichtiger Hinweis: Ihre Einkaufsliste wird nicht automatisch aktualisiert, wenn Sie Änderungen an Ihrem Mealplan vornehmen oder einen neuen Mealplan erstellen.\nUm sicherzustellen, dass Ihre Einkaufsliste alle benötigten Zutaten enthält, überprüfen Sie diese bitte regelmäßig und passen Sie sie manuell an.\nFür Fragen oder Unterstützung stehen wir Ihnen jederzeit zur Verfügung!")
    }
}