package ui.navigations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.compose.KoinContext
import org.lighthousegames.logging.logging
import viewModel.RecipeViewModel
import viewModel.SyncWorkerViewModel
import ui.components.RecipeCard
import ui.screens.authScreens.addNote
import ui.screens.authScreens.home.home
import ui.screens.authScreens.mealPlan.mealPlan
import ui.screens.authScreens.mealPlan.recipesComposable
import ui.screens.authScreens.medication.addMedication
import ui.screens.authScreens.medication.editMedication
import ui.screens.authScreens.medication.medication
import ui.screens.authScreens.settings.settingsComposable
import ui.screens.authScreens.timer.addTimerComposable
import ui.screens.authScreens.timer.editTimerComposable
import ui.screens.authScreens.timer.timerComposable
import ui.screens.authScreens.waterIntake.waterIntake
import ui.screens.authScreens.weights.addWeight.addWeight
import ui.screens.authScreens.weights.showAllWeights.showAllWeights
import ui.screens.authScreens.weights.weight
import ui.screens.launchScreen
import ui.screens.publicScreens.forgotPassword
import ui.screens.publicScreens.login
import ui.screens.publicScreens.signUp
import viewModel.FirebaseAuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    makeToast: (String) -> Unit,
) {
    logging().info { "NavGraph" }

    val user = Firebase.auth.currentUser

    val syncWorker = viewModel<SyncWorkerViewModel>()
    val firebaseAuthViewModel = viewModel<FirebaseAuthViewModel>()

    val minimumDelay by syncWorker.uiState.value.minimumDelay.collectAsState()
    val isFinishedSyncing by syncWorker.uiState.value.isFinishedSyncing.collectAsState()
    val hasError by syncWorker.uiState.value.hasError.collectAsState()

    val showContentInDevelopment: Boolean = false
    KoinContext {

        LaunchScreenDataSyncController(
            minimumDelay = minimumDelay,
            isFinishedSyncing = isFinishedSyncing,
            hasError = hasError,
            user = user,
            navController = navController
        ) {
            makeToast(it)
        }

        NavHost(navController = navController, startDestination = Destination.Launch.route) {
            publicScreens(navController, firebaseAuthViewModel, showContentInDevelopment)
            authScreens(navController, firebaseAuthViewModel, showContentInDevelopment)
        }
    }
}





fun NavGraphBuilder.publicScreens(
    navController: NavHostController,
    firebaseAuthViewModel: FirebaseAuthViewModel,
    showContentInDevelopment: Boolean
) {
    launchScreen()
    login(navController, firebaseAuthViewModel)
    forgotPassword(navController, firebaseAuthViewModel)
    signUp(navController, firebaseAuthViewModel)
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.authScreens(
    navController: NavHostController,
    firebaseAuthViewModel: FirebaseAuthViewModel,
    showContentInDevelopment: Boolean
) {
    home(navController, showContentInDevelopment, firebaseAuthViewModel)
    mealPlan(navController, firebaseAuthViewModel)

    addNote(navController, firebaseAuthViewModel)

    weight(navController)
    addWeight(navController)
    showAllWeights(navController)

    waterIntake(navController)

    medication(navController)
    addMedication(navController)
    editMedication(navController)

    timerComposable(navController)
    addTimerComposable(navController)
    editTimerComposable(navController)

    recipesComposable(navController)

    settingsComposable(navController, firebaseAuthViewModel)
}