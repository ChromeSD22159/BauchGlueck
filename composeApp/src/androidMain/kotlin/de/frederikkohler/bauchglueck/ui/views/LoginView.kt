package de.frederikkohler.bauchglueck.ui.views

import LoginViewModel
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
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.ui.components.BackgroundBlobWithStomach
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.icerock.moko.mvvm.flow.compose.observeAsActions

@Preview
@Composable
fun LoginView(
    loginViewModel: LoginViewModel = viewModel()
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
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            loginViewModel.onCancelButtonPressed()
                        }
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        enabled = isButtonEnabled,
                        onClick = {
                            loginViewModel.onLoginButtonPressed()
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
        LoginView()
    }
}