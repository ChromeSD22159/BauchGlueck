package de.frederikkohler.bauchglueck.ui.views

import viewModels.LoginViewModel
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frederikkohler.bauchglueck.ui.theme.displayFontFamily
import dev.icerock.moko.mvvm.flow.compose.observeAsActions
import model.LoginNav

@Composable
fun LoginView(
    onNavigate: (LoginNav) -> Unit,
    loginViewModel: LoginViewModel = viewModel(),
    firebaseViewModel: FirebaseAuthViewModel = viewModel()
) {
    val mail by loginViewModel.mail.collectAsStateWithLifecycle()
    val password by loginViewModel.password.collectAsStateWithLifecycle()
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
            verticalArrangement = Arrangement.Center,
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
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = displayFontFamily
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Mit deinem Konto anmelden!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            TextField(
                value = mail,
                onValueChange = { loginViewModel.mail.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                label = { Text("Enter text") },
                maxLines = 2,
                textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
                //shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TextField(
                value = password,
                modifier = Modifier
                    .fillMaxWidth(),
                onValueChange = { loginViewModel.password.value = it },
                label = { Text("Enter password") },
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
                            onNavigate(LoginNav.SignUp)
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
                                    onNavigate(LoginNav.Logged)
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

@Preview
@Composable
fun LoginViewPreview() {
    AppTheme {
        LoginView({})
    }
}