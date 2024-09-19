package ui.screens.authScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_gear
import ui.components.FormScreens.FormTextFieldWithoutIcons
import ui.components.theme.ScreenHolder
import ui.components.theme.button.IconButton
import ui.components.theme.button.TextButton
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.navigations.Destination
import ui.navigations.NavigationTransition
import util.DateRepository
import util.toDateString
import viewModel.AddNodeViewModel
import viewModel.FirebaseAuthViewModel

fun NavGraphBuilder.addNote(
    navController: NavHostController,
    firebaseAuthViewModel: FirebaseAuthViewModel
) {

    composable(
        route = Destination.AddNote.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {

        val viewModel = viewModel<AddNodeViewModel>()
        val allNotes by viewModel.allMoods.collectAsStateWithLifecycle()
        val message by viewModel.message.collectAsStateWithLifecycle()
        val currentMoods by viewModel.currentMoods.collectAsStateWithLifecycle()
        val node by viewModel.node.collectAsStateWithLifecycle()
        val userNodes by viewModel.userNodes.collectAsStateWithLifecycle(emptyList())

        ScreenHolder(
            title = Destination.AddNote.title,
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
            Column {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BodyText("${DateRepository.dayOfWeekName}, ${DateRepository.today.toDateString()}")

                    BodyText("(Moods: ${currentMoods.size})")

                    BodyText("(Notes: ${userNodes.size})")
                }

                FormTextFieldWithoutIcons(
                    inputValue = node,
                    onValueChange = {
                        viewModel.updateNodeText(it)
                    },
                    minLines = 5
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FooterText(text = viewModel.textFieldDisplayLength)
                }

                Spacer(Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(
                        text = "Abbrechen",
                        onClick = {
                            navController.navigate(Destination.Home.route)
                        }
                    )

                    TextButton(
                        text = "Speichern",
                        onClick = {
                            viewModel.saveNode { navController.navigate(Destination.Home.route) }
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))

                LazyVerticalGrid(
                    modifier = Modifier.heightIn(max = (allNotes.size * 25).dp),
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(allNotes.size) {

                        val moodIsInNodeList = viewModel.currentMoodListContainsMood(it)
                        val color = if (moodIsInNodeList) MaterialTheme.colorScheme.onBackground.copy(0.15f) else MaterialTheme.colorScheme.onBackground.copy(0.05f)
                        Row(
                            modifier = Modifier
                                .clickableWithRipple { viewModel.onClickOnMood(it) }
                                .fillMaxWidth()
                                .background(
                                    color = color,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            BodyText(
                                modifier = Modifier.fillMaxWidth(),
                                text = allNotes[it].display
                            )
                        }
                    }
                }
            }
        }
    }
}





