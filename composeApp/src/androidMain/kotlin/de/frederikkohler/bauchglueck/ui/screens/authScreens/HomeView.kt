package de.frederikkohler.bauchglueck.ui.screens.authScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.RoundImageButton
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import data.FirebaseConnection
import de.frederikkohler.bauchglueck.ui.screens.authScreens.settingsSheet.SettingSheet

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeView(
    firebaseAuthViewModel: FirebaseAuthViewModel = viewModel()
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        ScaffoldExample()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldExample(
    firebaseAuthViewModel: FirebaseAuthViewModel = viewModel()
) {
    var showSettingSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                title = {
                    Text("BauchGlück")
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        RoundImageButton(R.drawable.icon_gear) {
                            showSettingSheet = true
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val user by firebaseAuthViewModel.userProfile.collectAsStateWithLifecycle()

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                modifier = Modifier,
                text =
                """
                    This is an example of a scaffold. It uses the Scaffold composable's parameters to create a screen with a simple top app bar, bottom app bar, and floating action button.

                    It also contains some basic inner content, such as this text.
                """.trimIndent(),
            )

            SettingSheet(
                showSettingSheet = showSettingSheet,
                onDismissRequest = {
                    showSettingSheet = false
                    firebaseAuthViewModel.saveUserProfile(FirebaseConnection.Remote)
                },
                firebaseAuthViewModel = firebaseAuthViewModel
            )
        }
    }
}


enum class HomeNavigationItem(val route: String) {
    Home("home"), Settings("settings")
}