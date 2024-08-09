package de.frederikkohler.bauchglueck.ui.views

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.ui.components.BackgroundBlobWithStomach
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.icerock.moko.mvvm.flow.compose.observeAsActions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.frederikkohler.bauchglueck.ui.theme.displayFontFamily
import model.LoginNav
import viewModels.RegisterViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(
    onNavigate: (LoginNav) -> Unit,
    registerViewModel: RegisterViewModel = viewModel(),
    firebaseAuthViewModel: FirebaseAuthViewModel = viewModel()
) {
    val mail by registerViewModel.mail.collectAsStateWithLifecycle()
    val password by registerViewModel.password.collectAsStateWithLifecycle()
    val reEnterPassword by registerViewModel.reEnterPassword.collectAsStateWithLifecycle()
    val isProcessing by registerViewModel.isProcessing.collectAsStateWithLifecycle()
    val isButtonEnabled by registerViewModel.isButtonEnabled.collectAsStateWithLifecycle()
    val firstName by registerViewModel.firstName.collectAsStateWithLifecycle()
    val lastName by registerViewModel.lastName.collectAsStateWithLifecycle()
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
            verticalArrangement = Arrangement.Center,
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
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = displayFontFamily
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                )

                Text(
                    text = "Erstelle ein Konto!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            TextField(
                value = firstName,
                onValueChange = { registerViewModel.firstName.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                label = { Text("Vorname") },
                maxLines = 2,
                textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextField(
                value = lastName,
                onValueChange = { registerViewModel.lastName.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                label = { Text("Nachname") },
                maxLines = 2,
                textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextField(
                value = mail,
                onValueChange = { registerViewModel.mail.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                label = {
                    Text("E-Mail")
                },
                maxLines = 2,
                //textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
            )

            Spacer(modifier = Modifier.padding(8.dp))

            val selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
            val formattedDate = remember {
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(selectedDate))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Operatonsdatum:")

                Button(onClick = { showDatePicker = true }) {
                    Text("$formattedDate")
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

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

            TextField(
                value = reEnterPassword,
                modifier = Modifier
                    .fillMaxWidth(),
                onValueChange = { registerViewModel.password.value = it },
                label = { Text("Passwort") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextField(
                value = password,
                modifier = Modifier
                    .fillMaxWidth(),
                onValueChange = { registerViewModel.reEnterPassword.value = it },
                label = { Text("Passwort wiederholen") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.padding(16.dp))


            if (isProcessing) {
                CircularProgressIndicator()
            } else {
                Row(
                    modifier = Modifier.align(Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onNavigate(LoginNav.Login)
                            firebaseAuthViewModel.navigateTo(LoginNav.Login)
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
                                                onNavigate(LoginNav.Login)
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