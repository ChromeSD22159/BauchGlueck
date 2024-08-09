package de.frederikkohler.bauchglueck.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frederikkohler.bauchglueck.androidViewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.theme.AppTheme


@Composable
fun HomeView(
    firebaseAuthViewModel: FirebaseAuthViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Top
    ) {
        Button(
            onClick = {
                firebaseAuthViewModel.signOut()
            }
        ) {
            Text("Sign out")
        }
    }

}

@Preview
@Composable
fun HomePreview(){
    AppTheme {
        HomeView()
    }
}