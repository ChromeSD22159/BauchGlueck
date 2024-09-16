package ui.screens.publicScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_mail_fill
import bauchglueck.composeapp.generated.resources.magen
import org.jetbrains.compose.resources.painterResource
import org.lighthousegames.logging.logging
import ui.components.FormScreens.FormPasswordTextFieldWithIcon
import ui.components.FormScreens.FormTextFieldWithIcon
import ui.components.clickableWithRipple
import ui.components.theme.AppBackground
import ui.components.theme.background.AppBackgroundWithImage
import ui.components.theme.button.IconButton
import ui.components.theme.button.TextButton
import ui.components.theme.text.BodyText
import ui.components.theme.text.ErrorText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import viewModel.FirebaseAuthViewModel

@Composable
fun LoginView(
    navController: NavHostController,
    firebaseViewModel: FirebaseAuthViewModel,
) {

    val state = firebaseViewModel.userFormState.collectAsStateWithLifecycle()

    AppBackground  {
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

                HeadlineText(
                    text = "Willkommen zurück!",
                    color = MaterialTheme.colorScheme.primary,
                )

                BodyText(
                    text = "Mit deinem Konto anmelden!",
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
                    TextButton("Zur Registrierung") {
                        navController.navigate(Destination.SignUp.route)
                    }

                    IconButton {
                       firebaseViewModel.onLogin {
                           if (it.user != null) {
                               logging().info { "Login successful" }
                               navController.navigate(Destination.Home.route)
                           }
                       }
                    }
                }

                ErrorText(
                    text = firebaseViewModel.userFormState.value.error,
                    color = MaterialTheme.colorScheme.error
                )
            }

            BodyText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickableWithRipple { navController.navigate(Destination.ForgotPassword.route) },
                text = "Passwort vergessen?",
            )

            Spacer(modifier = Modifier)
        }
    }
}

