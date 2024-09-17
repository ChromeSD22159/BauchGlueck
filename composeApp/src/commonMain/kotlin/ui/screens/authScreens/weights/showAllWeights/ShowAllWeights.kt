package ui.screens.authScreens.weights.showAllWeights

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_timer
import bauchglueck.composeapp.generated.resources.ic_gear
import data.local.entitiy.Weight
import ui.components.theme.clickableWithRipple
import ui.navigations.Destination
import ui.screens.authScreens.weights.components.DeleteDialogManager
import kotlinx.datetime.LocalDate
import ui.components.theme.button.IconButton
import ui.components.theme.ScreenHolder
import util.toLocalDate
import viewModel.WeightScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.showAllWeights(navController: NavHostController) {
    composable(Destination.ShowAllWeights.route) {
        val viewModel = viewModel<WeightScreenViewModel>()
        val weights by viewModel.allWeights.collectAsStateWithLifecycle(initialValue = emptyList())

        ScreenHolder(
            title = Destination.ShowAllWeights.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.Weight.route)
            },
            optionsRow = {
                IconButton(
                    resource = Res.drawable.ic_add_timer,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.AddWeight.route)
                }

                IconButton(
                    resource = Res.drawable.ic_gear,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.Settings.route)
                }
            },
        ) {
            weights.forEach {weight ->
                WeightRowItem(weight) {
                    viewModel.softDelete(it)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightRowItem(
    weight: Weight,
    onDelete: (Weight) -> Unit = {}
) {
    val openAlertDialog: MutableState<Boolean> = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            .padding(16.dp)
            .clickableWithRipple {
                openAlertDialog.value = true
            }
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = weight.weighed.toLocalDate().toDateString())
        Text(text = "%.1f".format(weight.value) + "kg")
    }

    DeleteDialogManager(
        "${weight.value}kg",
        openAlertDialog
    ) {
        if (it) {
            onDelete(weight)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.toDateString(): String {
    val date = this
    val day = date.dayOfMonth.toString().padStart(2, '0')
    val month = date.month.value.toString().padStart(2, '0')
    val year = date.year.toString().substring(2..3)

    return "$day.${month}.$year"
}