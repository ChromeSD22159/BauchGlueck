package ui.navigations

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
    navController: NavHostController,
    makeToast: (String) -> Unit,
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
            makeToast("Keine Serververbindung")
        }
    }
}