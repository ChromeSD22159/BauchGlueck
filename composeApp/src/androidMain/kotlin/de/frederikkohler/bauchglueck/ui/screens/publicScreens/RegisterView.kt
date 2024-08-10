package de.frederikkohler.bauchglueck.ui.screens.publicScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.ui.components.BackgroundBlobWithStomach
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.icerock.moko.mvvm.flow.compose.observeAsActions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.components.CustomTextField
import de.frederikkohler.bauchglueck.ui.theme.displayFontFamily
import navigation.PublicNav
import viewModel.RegisterViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(
    onNavigate: (PublicNav) -> Unit,
    registerViewModel: RegisterViewModel = viewModel(),
    firebaseAuthViewModel: FirebaseAuthViewModel = viewModel()
) {
    val isProcessing by registerViewModel.isProcessing.collectAsStateWithLifecycle()
    val isButtonEnabled by registerViewModel.isButtonEnabled.collectAsStateWithLifecycle()
    val modalBottomSheetState = rememberModalBottomSheetState()
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    registerViewModel.actions.observeAsActions { action ->
        Toast.makeText(context, action.toString(), Toast.LENGTH_LONG).show()
    }

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
                    text = "Hallo!",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = displayFontFamily
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                )

                Text(
                    text = "Erstelle ein Konto!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
            }

            CustomTextField("Vorname", registerViewModel.firstName) {
                registerViewModel.firstName.value = it
            }

            CustomTextField("Nachname", registerViewModel.lastName) {
                registerViewModel.lastName.value = it
            }

            CustomTextField("E-Mail", registerViewModel.mail) {
                registerViewModel.mail.value = it
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Operatonsdatum:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                val selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                val formattedDate = remember {
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(selectedDate))
                }

                Button(
                    onClick = { showDatePicker = true }
                ) {
                    Text(formattedDate)
                }
            }

            CustomTextField("Passwort", registerViewModel.password) {
                registerViewModel.password.value = it
            }

            CustomTextField("Passwort wiederholen", registerViewModel.reEnterPassword) {
                registerViewModel.reEnterPassword.value = it
            }

            if (isProcessing) {
                CircularProgressIndicator()
            } else {
                Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onNavigate(PublicNav.Login)
                            firebaseAuthViewModel.navigateTo(PublicNav.Login)
                        }
                    ) {
                        Text("Zum Login")
                    }

                    Button(
                        enabled = isButtonEnabled,

                        onClick = {
                            registerViewModel.onRegisterButtonPressed(
                                action = { registerState ->
                                    firebaseAuthViewModel.signUp(
                                        registerState.mail,
                                        registerState.password,
                                        complete = {
                                            it.onSuccess {
                                                onNavigate(PublicNav.Login)
                                                registerState.isSignedIn = false
                                            }
                                        }
                                    )

                                    return@onRegisterButtonPressed registerState
                                }
                            )
                        }
                    ) {
                        Text("Registrieren")
                    }
                }
            }

            if (showDatePicker) {
                ModalBottomSheet(
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
                        Text("Bestätigen")
                    }
                }
            }

            Spacer(modifier = Modifier)
        }
    }
}

@Preview
@Composable
fun RegisterViewPreview() {
    AppTheme(darkTheme = false) {
        RegisterView({})
    }
}