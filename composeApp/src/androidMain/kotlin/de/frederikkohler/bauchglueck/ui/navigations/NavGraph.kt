package de.frederikkohler.bauchglueck.ui.navigations

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.stromarch
import coil3.compose.AsyncImage
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.screens.LaunchScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.meals.CalendarScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.home.HomeScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.medication.AddEditMedicationScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.medication.MedicationScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.AddEditTimerSheet
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.TimerScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.weights.addWeight.AddWeightScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.weights.showAllWeights.ShowAllWeights
import de.frederikkohler.bauchglueck.ui.screens.authScreens.weights.WeightScreen
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.LoginView
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.RegisterView
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import di.serverHost
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinContext
import org.lighthousegames.logging.logging
import org.koin.androidx.compose.koinViewModel
import viewModel.RecipeViewModel
import viewModel.SyncWorkerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: FirebaseAuthViewModel,
    appContext: Context,
) {
    logging().info { "NavGraph" }

    val user = Firebase.auth.currentUser

    val syncWorker = koinViewModel<SyncWorkerViewModel>()

    val minimumDelay by syncWorker.uiState.value.minimumDelay.collectAsState()
    val isFinishedSyncing by syncWorker.uiState.value.isFinishedSyncing.collectAsState()
    val hasError by syncWorker.uiState.value.hasError.collectAsState()





    KoinContext {

        LaunchScreenDataSyncController(
            minimumDelay = minimumDelay,
            isFinishedSyncing = isFinishedSyncing,
            hasError = hasError,
            user = user,
            appContext = appContext,
            navController = navController
        )

        NavHost(navController = navController, startDestination = Destination.Launch.route) {
            composable(Destination.Launch.route) {
                LaunchScreen()
            }
            composable(Destination.Login.route) {
                LoginView( { navController.navigate(it.route) } )
            }
            composable(Destination.SignUp.route) {
                RegisterView( { navController.navigate(it.route) } )
            }
            composable(Destination.Home.route) {
                HomeScreen(
                    firebaseAuthViewModel = viewModel,
                    navController = navController
                )
            }
            composable(Destination.Calendar.route) {
                CalendarScreen(
                    navController = navController
                )
            }

            // WEIGHT
            composable(Destination.Weight.route) {
                WeightScreen(
                    navController = navController,
                    backNavigationDirection = Destination.Home
                )
            }
            composable(
                route = Destination.AddWeight.route,
                enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
                exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
            ) {
                AddWeightScreen(
                    navController = navController,
                    onDismiss = {
                        navController.navigate(Destination.Weight.route)
                    }
                )
            }
            composable(Destination.ShowAllWeights.route) {
                ShowAllWeights(
                    navController = navController
                )
            }



            // WATERINTAKE
            composable(Destination.WaterIntake.route) {
                BackScaffold(
                    title = Destination.WaterIntake.title,
                    navController = navController
                )
            }




            // Medication
            composable(Destination.Medication.route) {
                MedicationScreen(
                    navController = navController
                )
            }
            composable(
                route = Destination.AddMedication.route,
                enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
                exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
            ) {
                AddEditMedicationScreen(
                    navController = navController,
                    currentMedication = null
                )
            }
            composable(
                route = Destination.EditMedication.route,
                exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() },
                enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
            ) {
                AddEditMedicationScreen(
                    navController = navController,
                    currentMedication = navController.currentBackStackEntry?.savedStateHandle?.get<String>("medicationId")
                )
            }



            // TIMER
            timerComposable(navController)
            addTimerComposable(navController)
            editTimerComposable(navController)

            recipesComposable(navController)
        }
    }
}

fun NavGraphBuilder.recipesComposable(navController: NavController) {
    composable(Destination.Recipes.route) {
        val recipeViewModel = koinViewModel<RecipeViewModel>()

        val recipesState by recipeViewModel.recipes.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            recipeViewModel.fetchRecipes()
        }

        LaunchedEffect(recipesState) {
            logging().info { "recipesState: ${recipesState.size}" }
        }

        LazyColumn(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recipesState) { recipe ->
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(vertical = 8.dp)

                ) {

                    AsyncImage(
                        model = serverHost + recipe.mainImage.formats.small.url,
                        placeholder = painterResource(Res.drawable.stromarch),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                    )

                    Text(text = recipe.name)

                    Text(text = recipe.description)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.editTimerComposable(navController: NavController) {
    composable(
        route = Destination.AddTimer.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {
        AddEditTimerSheet(
            navController = navController,
            currentCountdownTimer = null,
            onDismiss = {
                navController.navigate(Destination.Timer.route)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.addTimerComposable(navController: NavController) {
    composable(
        route = Destination.AddTimer.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {
        AddEditTimerSheet(
            navController = navController,
            currentCountdownTimer = null,
            onDismiss = {
                navController.navigate(Destination.Timer.route)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.timerComposable(navController: NavController) {
    composable(Destination.Timer.route) {
        TimerScreen(
            navController = navController,
        )
    }
}