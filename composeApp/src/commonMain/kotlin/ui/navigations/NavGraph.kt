package ui.navigations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.compose.KoinContext
import org.lighthousegames.logging.logging
import viewModel.SyncWorkerViewModel
import ui.screens.authScreens.addNote
import ui.screens.authScreens.addRecipe.addRecipe
import ui.screens.authScreens.home.home
import ui.screens.authScreens.mealPlan.mealPlan
import ui.screens.authScreens.medication.addMedication
import ui.screens.authScreens.medication.editMedication
import ui.screens.authScreens.medication.medication
import ui.screens.authScreens.recipeDetail.recipeDetails
import ui.screens.authScreens.searchRecipes.searchRecipes
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
import viewModel.RecipeViewModel

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
    val recipeViewModel = viewModel<RecipeViewModel>()

    val minimumDelay by syncWorker.uiState.value.minimumDelay.collectAsState()
    val isFinishedSyncing by syncWorker.uiState.value.isFinishedSyncing.collectAsState()
    val hasError by syncWorker.uiState.value.hasError.collectAsState()

    val showContentInDevelopment: Boolean = true

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
            authScreens(navController, firebaseAuthViewModel, recipeViewModel, showContentInDevelopment)
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
    recipeViewModel: RecipeViewModel,
    showContentInDevelopment: Boolean
) {
    home(navController, showContentInDevelopment, firebaseAuthViewModel)

    mealPlan(navController, firebaseAuthViewModel)
    searchRecipes(navController, recipeViewModel)
    recipeDetails(navController, recipeViewModel)
    addRecipe(navController)

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

    //recipesComposable(navController)

    settingsComposable(navController, firebaseAuthViewModel)
}