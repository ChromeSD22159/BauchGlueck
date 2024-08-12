package de.frederikkohler.bauchglueck.ui.screens.authScreens.settingsSheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import data.FirebaseConnection
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurgeryDatePicker(
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    val user by firebaseAuthViewModel.userProfile.collectAsStateWithLifecycle()
    val modalBottomSheetState = rememberModalBottomSheetState()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = user?.surgeryDateTimeStamp ?: System.currentTimeMillis()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    Button(onClick = { showDatePicker = true }) {
        Text(
            SimpleDateFormat(
                "dd.MM.yyyy",
                Locale.getDefault()
            ).format(Date(datePickerState.selectedDateMillis ?: System.currentTimeMillis()))
        )
    }

    user?.let { userProfile ->
        if (showDatePicker) {
            ModalBottomSheet(
                onDismissRequest = {
                    showDatePicker = false

                    val updatedProfile = userProfile.copy(
                        surgeryDateTimeStamp = datePickerState.selectedDateMillis ?: 0L
                    )
                    firebaseAuthViewModel.updateUserProfile(updatedProfile)
                    firebaseAuthViewModel.saveUserProfile(FirebaseConnection.Remote)
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