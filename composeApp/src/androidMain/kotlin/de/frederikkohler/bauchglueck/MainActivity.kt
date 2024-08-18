package de.frederikkohler.bauchglueck

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import data.Repository
import data.local.getDatabase
import data.network.ServerHost
import de.frederikkohler.bauchglueck.ui.navigations.NavGraph
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.LoginView
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()

    private var scope = lifecycleScope

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = getDatabase(
            context = applicationContext
        )

        val repository = Repository(
            serverHost = ServerHost.LOCAL_SABINA.url,
            db = db
        )

        scope.launch(Dispatchers.IO) {
            repository.getTimer()
        }

        setSystemBars()

        setContent {
            val appData by repository.repositoryUiState.collectAsState()
            val navController: NavHostController = rememberNavController()

            Log.i("repositoryUiState:isLoading", appData.isLoading.toString())
            Log.i("repositoryUiState:error", appData.isLoading.toString())


            AppTheme {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NavGraph(navController, firebaseAuthViewModel, appData)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when the app resumes
        //firebaseAuthViewModel.syncFirebase()
    }

    override fun onPause() {
        super.onPause()
        //firebaseAuthViewModel.syncFirebase()
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

@Preview
@Composable
fun AppAndroidLightPreview() {
    AppTheme {
        LoginView({})
    }
}
