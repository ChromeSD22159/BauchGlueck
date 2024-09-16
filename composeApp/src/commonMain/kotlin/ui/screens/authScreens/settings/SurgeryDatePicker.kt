package ui.screens.authScreens.settings

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.frederikkohler.bauchglueck.R
import ui.components.theme.Section
import ui.components.theme.button.TextButton
import ui.components.theme.text.BodyText
import viewModel.FirebaseAuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SurgeryDatePicker(
    firebaseAuthViewModel: FirebaseAuthViewModel,
    onUpdate: (Long) -> Unit
) {
    val userFormState by firebaseAuthViewModel.userFormState.value.userProfile.collectAsStateWithLifecycle()
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = userFormState.surgeryDateTimeStamp
    )
    var showDatePicker by remember { mutableStateOf(false) }

    Section {
        BodyText("Operationsdatum:")

        TextButton(
            text = SimpleDateFormat(
                "dd.MM.yyyy",
                Locale.getDefault()
            ).format(Date(datePickerState.selectedDateMillis ?: System.currentTimeMillis()))
        ) { showDatePicker = true }
    }

    userFormState.let { userProfile ->
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
                onDismissRequest = {
                    showDatePicker = false
                    onUpdate(datePickerState.selectedDateMillis ?: 0)
                },
                sheetState = modalBottomSheetState
            ) {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth(),
                    colors = DatePickerDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        headlineContentColor = MaterialTheme.colorScheme.onBackground,
                    )
                )

                TextButton(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    text = "Bestätigen"
                ) {
                    showDatePicker = false
                    onUpdate(datePickerState.selectedDateMillis ?: 0)
                }

            }
        }
    }
}

