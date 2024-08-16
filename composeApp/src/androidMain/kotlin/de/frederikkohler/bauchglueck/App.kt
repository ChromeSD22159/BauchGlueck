package de.frederikkohler.bauchglueck

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import de.frederikkohler.bauchglueck.ui.navigations.PublicNavigation
import de.frederikkohler.bauchglueck.ui.screens.LaunchScreen
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    val navController: NavHostController = rememberNavController()
    val scope = rememberCoroutineScope()
    var showSplashScreen by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(700)
        showSplashScreen = false
    }

    AppTheme {
        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (showSplashScreen) {
                    LaunchScreen()
                }

                PublicNavigation(navController, firebaseAuthViewModel)
            }
        }
    }
}