package ui.navigations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth

@Composable
fun LaunchScreenDataSyncController(
    minimumDelay: Boolean,
    isFinishedSyncing: Boolean,
    hasError: Boolean,
    user: FirebaseUser? = Firebase.auth.currentUser,
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