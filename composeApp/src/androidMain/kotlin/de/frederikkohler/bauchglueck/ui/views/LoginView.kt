package de.frederikkohler.bauchglueck.ui.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.ui.components.BackgroundBlobWithStomach
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Preview
@Composable
fun LoginView() {
    //var viewModel: FirebaseAuthViewModel = viewModel { FirebaseAuthViewModel() }
    var mail by remember { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

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
                onValueChange = { mail = it },
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
                onValueChange = { password = it },
                label = { Text("Enter password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        //login(mail, password, viewModel)
                    }
                ) {
                    Text("Register")
                }

                Button(
                    onClick = {
                        //login(mail, password, viewModel)
                    }
                ) {
                    Text("Login")
                }
            }

            Spacer(modifier = Modifier)
        }
    }
}

fun login(mail: String, password: String, viewModel: FirebaseAuthViewModel) {
    viewModel.signIn(mail, password) { result ->
        Log.i("LoginView", "Sign in result: $result")
    }
}

@Preview
@Composable
fun LoginViewPreview() {
    AppTheme {
        LoginView()
    }
}