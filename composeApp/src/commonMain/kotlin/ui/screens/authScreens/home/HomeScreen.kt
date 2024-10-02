package ui.screens.authScreens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_gear
import bauchglueck.composeapp.generated.resources.ic_kochhut
import bauchglueck.composeapp.generated.resources.ic_meal_plan
import data.local.entitiy.TimerState
import ui.navigations.Destination
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.IconCard
import ui.components.theme.button.IconButton
import ui.components.theme.button.TextButton
import ui.components.theme.ScreenHolder
import ui.components.theme.Section
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.HeadlineText
import ui.navigations.NavKeys
import ui.navigations.setNavKey
import viewModel.FirebaseAuthViewModel
import viewModel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.home(
    navController: NavHostController,
    showContentInDevelopment: Boolean,
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    composable(Destination.Home.route) {backStackEntry ->

        val scope = rememberCoroutineScope()
        val viewModel = viewModel<HomeViewModel>()
        val medications by viewModel.medicationsWithIntakeDetailsForToday.collectAsStateWithLifecycle()
        val weeklyAverage by viewModel.weeklyAverage.collectAsStateWithLifecycle()
        val timers by viewModel.allTimers.collectAsStateWithLifecycle()
        val nextMedication by viewModel.nextMedicationIntake.collectAsStateWithLifecycle()

        ScreenHolder(
            title = Destination.Home.title,
            showBackButton = false,
            optionsRow = {
                IconButton(
                    resource = Res.drawable.ic_gear,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.Settings.route)
                }
            },
            pageSpacing = 0.dp,
            itemSpacing = 24.dp
        ) {

            SectionImageCard(
                image = Res.drawable.ic_meal_plan,
                title = "MealPlaner",
                description = "Erstelle deinen MealPlan, indifiduell auf deine bedürfnisse.",
                offset = DpOffset(0.dp, 0.dp),
                onNavigate = {
                    navController.navigate(Destination.MealPlanCalendar.route)
                }
            )

            SectionImageCard(
                image = Res.drawable.ic_kochhut,
                title = "Rezepte",
                offset = DpOffset(0.dp, 0.dp),
                description = "Stöbere durch rezepte und füge sie zu deinem Meal plan hinzu.",
                onNavigate = {
                    navController.navigate(Destination.SearchRecipe.route)
                    navController.setNavKey(NavKeys.Destination, Destination.Home.route)
                }
            )

            WeightCardChartCard(
                hasValidData = weeklyAverage.any { it.avgValue > 0.0 },
                navController = navController,
                weeklyAverage = weeklyAverage
            )

            HomeTimerWidget(
                timers.sortedBy { TimerState.fromValue(it.timerState).state  }
            ) {
                scope.launch {
                    navController.navigate(it.route)
                }
            }

            IconCard(
                height = 150.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                ) {
                    TextButton(
                        text = "Notiz eintragen"
                    ) {
                        navController.navigate(Destination.AddNote.route)
                    }

                    TextButton(
                        text = "Alle Notizen"
                    ) {
                        navController.navigate(Destination.ShowAllNotes.route)
                    }
                }
            }

            /*
            LastNotesCalendar(
                onNavigate = { destination, node ->
                    logging().info { "navigate to $destination" }
                    logging().info { "node: $node" }
                    navController.navigate(destination.route)
                    navController.currentBackStackEntry?.savedStateHandle?.set("noteId", "${node?.id}" )
                }
            )
             */

            WaterIntakeCard(firebaseAuthViewModel = firebaseAuthViewModel)

            NextMedicationCard(
                navController = navController,
                medications = medications,
                nextMedication = nextMedication
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun SectionImageCard(
    image: DrawableResource = Res.drawable.ic_kochhut,
    title: String,
    description:String,
    offset: DpOffset,
    onNavigate: () -> Unit,
) {
    Section(
        sectionPadding = 0.dp,
        sectionModifier = Modifier
            .padding(horizontal = 10.dp)
            .clickableWithRipple {
                onNavigate()
            }
    ) {
        Box(
            modifier = Modifier
                .height(100.dp),
            contentAlignment = Alignment.CenterEnd
        ) {

            Image(
                modifier = Modifier
                    .offset(offset.x, offset.y)
                    .alpha(0.25f)
                    .size(200.dp)
                    .rotate(15f),
                imageVector = vectorResource(resource = image),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeadlineText(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    size = 16.sp,
                    weight = FontWeight.Medium,
                    text = title
                )
                BodyText(
                    text = description,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}