package ui.screens.authScreens.weights.showAllWeights

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavController
import data.local.entitiy.Weight
import ui.components.BackScaffold
import ui.components.clickableWithRipple
import ui.navigations.Destination
import ui.screens.authScreens.weights.components.DeleteDialogManager
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel
import util.toLocalDate
import viewModel.WeightScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowAllWeights(
    navController: NavController,
) {
    val viewModel = koinViewModel<WeightScreenViewModel>()
    val weights by viewModel.allWeights.collectAsStateWithLifecycle(initialValue = emptyList())

    BackScaffold(
        title = Destination.ShowAllWeights.title,
        navController = navController,
        isLazyColumn = true,
        lazy = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .wrapContentHeight(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allWeights(weights) { deleteWeight ->
                    viewModel.softDelete(deleteWeight)
                }
            }
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.allWeights(
    weights: List<Weight>,
    onDelete: (Weight) -> Unit = {},
) {
    stickyHeader {
        Text(
            text = "Alle Gewichtseinträge",
            modifier = Modifier
                .padding(top = 8.dp)
                .background(Color.Gray.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small)
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),
        )
    }
    items(weights, key = { item -> item.weightId }) { weight ->
        WeightRowItem(weight) {
            onDelete(it)
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