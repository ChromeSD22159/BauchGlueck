package ui.screens.publicScreens

import android.util.Patterns
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_mail_fill
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.components.BackgroundBlobWithStomach
import ui.components.FormScreens.FormTextFieldWithIcon
import ui.components.clickableWithRipple
import ui.navigations.Destination
import ui.theme.displayFontFamily
import viewModel.FirebaseAuthViewModel

@Composable
fun ForgotPasswordScreen(
    firebaseViewModel: FirebaseAuthViewModel,
    onNavigate: (Destination) -> Unit
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val email = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }
    val isProcessing = remember { mutableStateOf(false) }

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
                    text = "Passwort vergessen?",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = displayFontFamily
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Lass dir einen Link schicken um,\ndein Passwort zurückzusetzen!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
            }

            FormTextFieldWithIcon(
                leadingIcon = Res.drawable.ic_mail_fill,
                inputValue = email.value,
                onValueChange = {
                    email.value = it
                }
            )

            if (isProcessing.value) {
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
                        Text("Zurück")
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                if (!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                                    message.value = "Bitte gib deine E-Mail ein!"
                                    delay(5000)
                                    message.value = ""
                                    return@launch
                                } else {
                                    try {
                                        isProcessing.value = true
                                        firebaseViewModel.forgotPassword(email.value)
                                        isProcessing.value = false
                                        message.value = "E-Mail wurde gesendet!"
                                        delay(5000)
                                        message.value = ""
                                    } catch (e: Exception) {
                                        message.value = "Fehler beim Senden der E-Mail: ${e.message}"
                                        delay(5000)
                                        message.value = ""
                                    }
                                }
                            }
                        }
                    ) {
                        Text("E-Mail anfordern")
                    }
                }

                Text(text = message.value)
            }

            Spacer(modifier = Modifier)
        }
    }
}