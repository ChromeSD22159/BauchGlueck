package de.frederikkohler.bauchglueck.ui.screens.publicScreens

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
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.frederikkohler.bauchglueck.ui.components.BackgroundBlobWithStomach
import de.frederikkohler.bauchglueck.ui.components.FormScreens.FormTextFieldWithIcon
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import de.frederikkohler.bauchglueck.ui.theme.displayFontFamily
import viewModel.FirebaseAuthViewModel

@Composable
fun LoginView(
    firebaseViewModel: FirebaseAuthViewModel,
    onNavigate: (Destination) -> Unit
) {

    val state = firebaseViewModel.userFormState.collectAsStateWithLifecycle()

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
            Spacer(modifier = Modifier)

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Willkommen zurück!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = displayFontFamily
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Mit deinem Konto anmelden!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
            }

            FormTextFieldWithIcon(
                inputValue = state.value.email,
                onValueChange = {
                    firebaseViewModel.onChangeEmail(it)
                }
            )

            FormTextFieldWithIcon(
                inputValue = state.value.password,
                onValueChange = {
                    firebaseViewModel.onChangePassword(it)
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
                            onNavigate(Destination.SignUp)
                        }
                    ) {
                        Text("Zur Registrierung")
                    }

                    // TODO: Add Forgot Password Button
                    Button(
                        enabled = true,
                        onClick = {
                            val result = firebaseViewModel.onLogin()
                            if (result.isSuccess) {
                                onNavigate(Destination.Home)
                            }
                        }
                    ) {
                        Text("Login")
                    }
                }

                Text(text = firebaseViewModel.userFormState.value.error)
            }

            Spacer(modifier = Modifier)
        }
    }
}
