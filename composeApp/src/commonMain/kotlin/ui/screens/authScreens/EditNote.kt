package ui.screens.authScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.lighthousegames.logging.logging
import ui.components.FormScreens.FormTextFieldWithoutIcons
import ui.components.theme.ScreenHolder
import ui.components.theme.button.IconButton
import ui.components.theme.button.TextButton
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.navigations.Destination
import util.Weekday
import util.displayDate
import viewModel.AddNoteViewModel

@OptIn(ExperimentalLayoutApi::class)
fun NavGraphBuilder.editNote(
    navController: NavHostController
){
    composable(
        route = Destination.EditNote.route,
    ) {

        val noteId = it.savedStateHandle.get<String>("noteId")
        val viewModel = viewModel<AddNoteViewModel>()
        val allMoods by viewModel.allMoods.collectAsState()
        val text by viewModel.node.collectAsStateWithLifecycle()
        val selectedNode by viewModel.currentNote.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            if (noteId != null) {
                viewModel.setNoteId(noteId)
            }
        }
        ScreenHolder(
            title = Destination.EditNote.title,
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
                    selectedNode?.let { note ->
                       val date = Instant.fromEpochMilliseconds(note.date).toLocalDateTime(TimeZone.currentSystemDefault())
                        BodyText("${Weekday.fromInt(date.dayOfWeek.ordinal)}, ${date.displayDate}")
                    }
                }

                FormTextFieldWithoutIcons(
                    inputValue = text,
                    onValueChange = { textInput ->
                        viewModel.updateNodeText(textInput)
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
                            viewModel.saveUpdatedNote { navController.navigate(Destination.Home.route) }
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))


                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    allMoods.forEachIndexed { index, mood ->
                        val moodIsInNodeList = viewModel.currentMoodListContainsMood(index)
                        val color = if (moodIsInNodeList) MaterialTheme.colorScheme.onBackground.copy(0.15f) else MaterialTheme.colorScheme.onBackground.copy(0.05f )
                        Row(
                            modifier = Modifier
                                .clickableWithRipple { viewModel.onClickOnMood(index) }
                                .background(
                                    color = color,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FooterText(
                                modifier = Modifier,
                                text = mood.display
                            )
                        }
                    }
                }
            }
        }
    }
}