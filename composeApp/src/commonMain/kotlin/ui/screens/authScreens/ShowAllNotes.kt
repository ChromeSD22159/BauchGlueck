package ui.screens.authScreens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_gear
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ui.components.theme.ScreenHolder
import ui.components.theme.Section
import ui.components.theme.button.IconButton
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.navigations.Destination
import ui.navigations.NavigationTransition
import util.displayTime
import viewModel.ShowAllNotesViewModel

@OptIn(ExperimentalLayoutApi::class)
fun NavGraphBuilder.showAllNotes(
    navController: NavHostController
) {

    composable(
        route = Destination.ShowAllNotes.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {

        val viewModel = viewModel<ShowAllNotesViewModel>()
        val allNotes by viewModel.allNotes.collectAsStateWithLifecycle()

        ScreenHolder(
            title = Destination.ShowAllNotes.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.Home.route)
            },
            optionsRow = {
                IconButton(
                    resource = Res.drawable.ic_gear,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.Settings.route)
                }
            }
        ) {
            allNotes.forEach {
                Section {
                    Column(
                        modifier = Modifier
                            .clickableWithRipple {
                                navController.navigate(Destination.EditNote.route)
                                navController.currentBackStackEntry?.savedStateHandle?.set("noteId", it.nodeId)
                            }
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                       Row(
                           modifier = Modifier.fillMaxWidth(),
                           horizontalArrangement = Arrangement.End
                       ) {

                           val date = Instant.fromEpochMilliseconds(it.date).toLocalDateTime(TimeZone.currentSystemDefault())
                           FooterText("${date.dayOfMonth}.${date.monthNumber}.${date.year} ${date.displayTime}")
                       }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BodyText("Notiz:")
                            BodyText(it.text)
                        }


                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            FooterText("Moods:")
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                it.moods.forEach { mood ->
                                    BodyText(mood.display)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}