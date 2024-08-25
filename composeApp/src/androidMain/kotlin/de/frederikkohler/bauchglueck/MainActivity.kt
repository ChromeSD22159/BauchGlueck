package de.frederikkohler.bauchglueck

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import data.network.isServerReachable
import de.frederikkohler.bauchglueck.ui.navigations.NavGraph
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import di.KoinInject
import kotlinx.coroutines.launch
import org.koin.compose.currentKoinScope
import org.lighthousegames.logging.logging
import viewModel.TimerViewModel

class MainActivity : ComponentActivity() {

    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()
    private val timerViewModel by viewModels<TimerViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KoinInject(applicationContext).init()

        setSystemBars()

        lifecycleScope.launch {
            val isReachable = isServerReachable()
            if (isReachable) {
                // Server is reachable, proceed with your logic
                val msg = "Server is available"
                logging().info { msg }
            } else {
                // Server is not reachable, handle the error (e.g., show a message to the user)
                val msg = "Server is not available"
                logging().error { msg }
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            }
        }

        setContent {


            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController: NavHostController = rememberNavController()
                    val viewModel: TimerViewModel = org.koin.androidx.compose.koinViewModel()

                    LaunchedEffect(Unit) {
                        viewModel.updateLocalData()
                        viewModel.updateRemoteData()

                        viewModel.getAllCountdownTimers()
                    }

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NavGraph(navController, firebaseAuthViewModel) {

                        }
                    }
                }
            }
        }
    }

    private fun setSystemBars() {
        // Set decor to draw behind system bars (status bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set the status bar to transparent
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // Adjust the appearance of status bar icons and text
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        when (applicationContext.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                // Dark mode: Light text and icons on status bar
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                // Light mode: Dark text and icons on status bar
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                // Undefined mode: Use default setting (optional)
                controller.isAppearanceLightStatusBars = false // or true based on preference
                controller.isAppearanceLightNavigationBars = false // or true based on preference
            }
        }
    }
}

@Composable
inline fun <reified T: ViewModel> koinViewModel(): T {
    val scope = currentKoinScope()
    return viewModel {
        scope.get<T>()
    }
}