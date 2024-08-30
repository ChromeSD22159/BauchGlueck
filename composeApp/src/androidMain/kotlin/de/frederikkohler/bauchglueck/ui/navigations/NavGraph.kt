package de.frederikkohler.bauchglueck.ui.navigations

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.screens.LaunchScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.meals.CalendarScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.home.HomeScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.medication.MedicationScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.AddEditTimerSheet
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.TimerScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.weights.addWeight.AddWeightSheet
import de.frederikkohler.bauchglueck.ui.screens.authScreens.weights.showAllWeights.ShowAllWeights
import de.frederikkohler.bauchglueck.ui.screens.authScreens.weights.WeightScreen
import viewModel.TimerViewModel
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.LoginView
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.RegisterView
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.lighthousegames.logging.logging
import org.koin.androidx.compose.koinViewModel
import viewModel.SyncWorkerViewModel
import viewModel.WeightViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: FirebaseAuthViewModel,
    appContext: Context,
) {
    logging().info { "NavGraph" }

    val user = Firebase.auth.currentUser
    val scope = rememberCoroutineScope()
    val timerViewModel = koinViewModel<TimerViewModel>()
    val weightViewModel = koinViewModel<WeightViewModel>()
    val medicationViewModel = koinViewModel<TimerViewModel>()
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

        LaunchedEffect(Unit) {
            weightViewModel.getAllItems()
        }

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
                    timerViewModel = timerViewModel,
                    weightViewModel = weightViewModel,
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
                    backNavigationDirection = Destination.Home,
                    viewModel = weightViewModel,
                )
            }
            composable(
                route = Destination.AddWeight.route,
                enterTransition = { slideInWithFadeToTopAnimation() },
                exitTransition = { slideOutWithFadeToTopAnimation() }
            ) {
                weightViewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    weightViewModel.getLastWeight()
                }

                val lastWeight by weightViewModel.lastWeight.collectAsState()

                AddWeightSheet(
                    navController = navController,
                    lastWeight = lastWeight,
                    onDismiss = {
                        navController.navigate(Destination.Weight.route)
                    },
                    onSaved = {
                        scope.launch {
                            weightViewModel.addItem(it)
                            navController.navigate(Destination.Weight.route)
                        }
                    }
                )
            }
            composable(Destination.ShowAllWeights.route) {
                ShowAllWeights(
                    weightViewModel = weightViewModel,
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




            // TIMER
            composable(Destination.Timer.route) {
                TimerScreen(
                    navController = navController,
                    viewModel = timerViewModel,
                )
            }
            composable(
                route = Destination.AddTimer.route,
                enterTransition = { slideInWithFadeToTopAnimation() },
                exitTransition = { slideOutWithFadeToTopAnimation() }
            ) {
                AddEditTimerSheet(
                    navController = navController,
                    viewModel = timerViewModel,
                    currentCountdownTimer = null,
                    onDismiss = {
                        navController.navigate(Destination.Timer.route)
                    }
                )
            }
            composable(
                route = Destination.EditTimer.route,
                enterTransition = { slideInWithFadeToTopAnimation() },
                exitTransition = { slideOutWithFadeToTopAnimation() }
            ) {
                AddEditTimerSheet(
                    navController = navController,
                    viewModel = timerViewModel,
                    currentCountdownTimer = timerViewModel.selectedTimer.value,
                    onDismiss = {
                        navController.navigate(Destination.Timer.route)
                    }
                )
            }
        }
    }
}

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

fun slideInWithFadeToTopAnimation(): EnterTransition {
    return slideInVertically(
        initialOffsetY = { it.takeIf { it != Int.MIN_VALUE } ?: 0 },
        animationSpec = tween(250)
    ) + scaleIn(
        animationSpec = tween(250)
    ) + fadeIn(animationSpec = tween(250))
}

fun slideOutWithFadeToTopAnimation(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { it.takeIf { it != Int.MIN_VALUE } ?: 0 },
        animationSpec = tween(250)
    ) + scaleOut(
        animationSpec = tween(250)
    ) + fadeOut(animationSpec = tween(250))
}