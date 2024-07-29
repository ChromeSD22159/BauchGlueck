package de.frederikkohler.bauchglueck.ui.views

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginView() {
    val viewModel: FirebaseAuthViewModel = viewModel { FirebaseAuthViewModel() }
}
