package de.frederikkohler.bauchglueck.ui.screens.publicScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.ui.components.BackgroundBlobWithStomach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.koinViewModel
import de.frederikkohler.bauchglueck.ui.components.FormScreens.FormTextFieldWithIcon
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import de.frederikkohler.bauchglueck.ui.theme.displayFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterView(
    onNavigate: (Destination) -> Unit,
) {
    val firebaseViewModel = koinViewModel<viewModel.FirebaseAuthViewModel>()

    val state = firebaseViewModel.userFormState.collectAsStateWithLifecycle()

    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopEnd
    ) {
        BackgroundBlobWithStomach()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.register_hallo_text),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = displayFontFamily
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                )

                Text(
                    text = stringResource(R.string.register_create_account_text),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
            }

            FormTextFieldWithIcon(
                inputValue = state.value.firstName,
                onValueChange = {
                    firebaseViewModel.onChangeFirstName(it)
                }
            )

            FormTextFieldWithIcon(
                inputValue = state.value.lastName,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = {
                    firebaseViewModel.onChangeLastName(it)
                }
            )

            FormTextFieldWithIcon(
                inputValue = state.value.email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = {
                    firebaseViewModel.onChangeEmail(it)
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.register_surgery_date_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                val selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                val datePattern = stringResource(R.string.date_pattern)
                val formattedDate = remember {
                    SimpleDateFormat(datePattern, Locale.getDefault()).format(Date(selectedDate))
                }

                Button(
                    onClick = { showDatePicker = true }
                ) {
                    Text(formattedDate)
                }
            }

            FormTextFieldWithIcon(
                inputValue = state.value.password,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = {
                    firebaseViewModel.onChangePassword(it)
                }
            )

            FormTextFieldWithIcon(
                inputValue = state.value.confirmPassword,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = {
                    firebaseViewModel.onChangeConfirmPassword(it)
                }
            )

            if (state.value.isProcessing) {
                CircularProgressIndicator()
            } else {
                Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onNavigate(Destination.Login)
                        }
                    ) {
                        Text(stringResource(R.string.register_login_button_text))
                    }

                    Button(
                        onClick = {
                            firebaseViewModel.onSignUp()
                            onNavigate(Destination.Home)
                        }
                    ) {
                        Text(stringResource(R.string.register_register_button_text))
                    }
                }
                
                Text(text = firebaseViewModel.userFormState.value.error ?: "")
            }

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
                    onDismissRequest = { showDatePicker = false },
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

            Spacer(modifier = Modifier)
        }
    }
}