package de.frederikkohler.bauchglueck.ui.screens.publicScreens

import viewModel.LoginViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.ui.components.BackgroundBlobWithStomach
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.components.CustomTextField
import de.frederikkohler.bauchglueck.ui.theme.displayFontFamily
import dev.icerock.moko.mvvm.flow.compose.observeAsActions
import navigation.Screens

@Composable
fun LoginView(
    onNavigate: (Screens) -> Unit,
    loginViewModel: LoginViewModel = viewModel(),
    firebaseViewModel: FirebaseAuthViewModel = viewModel()
) {
    val isProcessing by loginViewModel.isProcessing.collectAsStateWithLifecycle()
    val isButtonEnabled by loginViewModel.isButtonEnabled.collectAsStateWithLifecycle()

    val context = LocalContext.current

    loginViewModel.actions.observeAsActions { action ->
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

            CustomTextField("Deine E-Mail", loginViewModel.mail) {
                loginViewModel.mail.value = it
            }

            CustomTextField("Dein Passwort", loginViewModel.password) {
                loginViewModel.password.value = it
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
                            onNavigate(Screens.SignUp)
                        }
                    ) {
                        Text("Zur Registrierung")
                    }

                    Button(
                        enabled = isButtonEnabled,
                        onClick = {
                            loginViewModel.onLoginButtonPressed(
                                action = { loginState ->
                                    firebaseViewModel.signIn(loginState.mail, loginState.password)
                                    loginState.isSignedIn = true
                                    onNavigate(Screens.Home)
                                    return@onLoginButtonPressed loginState
                                }
                            )
                        }
                    ) {
                        Text("Login")
                    }
                }
            }

            Spacer(modifier = Modifier)
        }
    }
}
