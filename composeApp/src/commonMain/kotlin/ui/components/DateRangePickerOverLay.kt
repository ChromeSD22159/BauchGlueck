package ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.components.theme.button.TextButton
import ui.components.theme.text.BodyText
import ui.components.theme.text.HeadlineText
import util.DateRepository

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DateRangePickerOverLay(
    showDatePicker: Boolean,
    onDatePickerStateChange: (Boolean) -> Unit,
    onConformDate: (startDate: Long?, endDate: Long?) -> Unit
) {

    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val today = DateRepository.startEndTodayCurrentTimeZone()

    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = today.start,
        initialSelectedEndDateMillis = today.start.plus(4 * 24 * 60 * 60 * 1000),
        selectableDates = object : SelectableDates {
            val fourteenDaysLater = today.start.plus(14 * 24 * 60 * 60 * 1000)
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in today.start..fourteenDaysLater
            }
        },
        initialDisplayMode = DisplayMode.Input
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
            DateRangePicker(
                headline = {},
                title = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HeadlineText(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Wähle deinen Einkaufszeitraum",
                            size = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        BodyText(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Dieser Zeitraum bestimmt, welche Lebensmittel für deine geplanten Mahlzeiten benötigt werden.",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                state = dateRangePickerState,
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
                val startDate = dateRangePickerState.selectedStartDateMillis
                val endDate = dateRangePickerState.selectedEndDateMillis
                onConformDate(startDate, endDate)
            }
        }
    }
}