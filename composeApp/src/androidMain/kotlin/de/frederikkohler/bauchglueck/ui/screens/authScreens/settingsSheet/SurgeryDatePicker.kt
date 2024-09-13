package de.frederikkohler.bauchglueck.ui.screens.authScreens.settingsSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.frederikkohler.bauchglueck.R
import viewModel.FirebaseAuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SurgeryDatePicker(
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    val userFormState by firebaseAuthViewModel.userFormState.collectAsStateWithLifecycle()
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = userFormState.userProfile?.surgeryDateTimeStamp ?: System.currentTimeMillis()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.register_surgery_date_text),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Button(onClick = { showDatePicker = true }) {
            Text(
                SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.getDefault()
                ).format(Date(datePickerState.selectedDateMillis ?: System.currentTimeMillis())) // 01.01.1970 0:00 -> Secunden
            )
        }
    }

    userFormState.currentUser?.let { userProfile ->
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
                    userFormState.userProfile?.let {
                        firebaseAuthViewModel.onUpdateUserProfile(it.copy(surgeryDateTimeStamp = datePickerState.selectedDateMillis ?: 0L))
                    }
                },
                sheetState = modalBottomSheetState
            ) {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        showDatePicker = false
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(stringResource(R.string.confirm_text))
                }

            }
        }
    }
}
