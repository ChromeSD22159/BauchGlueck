package ui.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import ui.components.theme.button.TextButton
import util.DateRepository

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DatePickerOverLay(
    showDatePicker: Boolean,
    onDatePickerStateChange: (Boolean) -> Unit,
    onConformDate: (selectedDateMillis: Long?) -> Unit
) {

    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = DateRepository.startEndTodayCurrentTimeZone().start,
        selectableDates = object : SelectableDates {
            val today = Clock.System.now().toEpochMilliseconds()
            val fourteenDaysLater = today.plus(14 * 24 * 60 * 60 * 1000)
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in today..fourteenDaysLater
            }
        }
    )

    if (showDatePicker) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    bottom = WindowInsets
                        .navigationBarsIgnoringVisibility
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
            onDismissRequest = { onDatePickerStateChange(false) },
            sheetState = modalBottomSheetState
        ) {
            androidx.compose.material3.DatePicker(
                state = datePickerState,
                modifier = Modifier.fillMaxWidth(),
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    headlineContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )

            TextButton(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                text = "Bestätigen"
            ) {
                onDatePickerStateChange(false)
                onConformDate(datePickerState.selectedDateMillis)
            }
        }
    }
}