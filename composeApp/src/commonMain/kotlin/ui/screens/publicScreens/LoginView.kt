package ui.screens.publicScreens

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_lock_fill
import bauchglueck.composeapp.generated.resources.ic_mail_fill
import ui.components.BackgroundBlobWithStomach
import ui.components.FormScreens.FormPasswordTextFieldWithIcon
import ui.components.FormScreens.FormTextFieldWithIcon
import ui.components.clickableWithRipple
import ui.navigations.Destination
import ui.theme.displayFontFamily
import viewModel.FirebaseAuthViewModel

@Composable
fun LoginView(
    navController: NavHostController,
    firebaseViewModel: FirebaseAuthViewModel,
    onNavigate: (Destination) -> Unit
) {

    val state = firebaseViewModel.userFormState.collectAsStateWithLifecycle()

    LaunchedEffect(firebaseViewModel.user) {
        if (firebaseViewModel.user != null) {
            onNavigate(Destination.Home)
        }
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

            FormTextFieldWithIcon(
                leadingIcon = Res.drawable.ic_mail_fill,
                inputValue = state.value.email,
                onValueChange = {
                    firebaseViewModel.onChangeEmail(it)
                }
            )

            FormPasswordTextFieldWithIcon(
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
                            firebaseViewModel.onLogin()
                        }
                    ) {
                        Text("Login")
                    }
                }

                Text(text = firebaseViewModel.userFormState.value.error)
            }

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickableWithRipple { navController.navigate(Destination.ForgotPassword.route) },
                text = "Passwort vergessen?"
            )

            Spacer(modifier = Modifier)
        }
    }
}
