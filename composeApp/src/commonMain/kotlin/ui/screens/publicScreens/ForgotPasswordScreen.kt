package ui.screens.publicScreens

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import ui.components.theme.button.TextButton
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_mail_fill
import bauchglueck.composeapp.generated.resources.magen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import ui.components.FormScreens.FormTextFieldWithIcon
import ui.components.theme.background.AppBackgroundWithImage
import ui.components.theme.text.BodyText
import ui.components.theme.text.ErrorText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import viewModel.FirebaseAuthViewModel

fun NavGraphBuilder.forgotPassword(navController: NavHostController, firebaseAuthViewModel: FirebaseAuthViewModel) {
    composable(Destination.ForgotPassword.route) {
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
            AppBackgroundWithImage()

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
                    Image(
                        painter = painterResource(Res.drawable.magen),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(top = 25.dp, end = 10.dp)
                            .width(150.dp)
                            .height(150.dp)
                    )

                    HeadlineText("Passwort vergessen?",
                        color = MaterialTheme.colorScheme.primary,
                    )

                    BodyText("Lass dir einen Link schicken um,\ndein Passwort zurückzusetzen!")
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

                        TextButton(text = "Zurück") {
                            navController.navigate(Destination.Login.route)
                        }

                        TextButton(text = "E-Mail anfordern!") {
                            coroutineScope.launch {
                                if (!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                                    message.value = "Bitte gib deine E-Mail ein!"
                                    delay(5000)
                                    message.value = ""
                                    return@launch
                                } else {
                                    try {
                                        isProcessing.value = true
                                        firebaseAuthViewModel.forgotPassword(email.value)
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
                    }

                    ErrorText(text = message.value)
                }

                Spacer(modifier = Modifier)
            }
        }
    }
}