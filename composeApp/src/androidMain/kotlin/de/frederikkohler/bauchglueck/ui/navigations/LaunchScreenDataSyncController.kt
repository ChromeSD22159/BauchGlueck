package de.frederikkohler.bauchglueck.ui.navigations

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import dev.gitlive.firebase.auth.FirebaseUser

@Composable
fun LaunchScreenDataSyncController(
    minimumDelay: Boolean,
    isFinishedSyncing: Boolean,
    hasError: Boolean,
    user: FirebaseUser?,
    appContext: Context,
    navController: NavHostController,
) {
    LaunchedEffect(minimumDelay, isFinishedSyncing, hasError, user) {
        if (minimumDelay && isFinishedSyncing) {
            if (user != null) {
                navController.navigate(Destination.Home.route)
            } else {
                navController.navigate(Destination.Login.route)
            }
        }

        if (minimumDelay && hasError) {
            Toast.makeText(appContext, "Keine Serververbindung", Toast.LENGTH_SHORT).show()
        }
    }
}