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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import data.Repository
import data.local.LocalDatabase
import data.local.getDatabase
import data.network.ServerHost
import de.frederikkohler.bauchglueck.ui.navigations.NavGraph
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import util.KeyValueStorage
import util.onError
import util.onSuccess

class MainActivity : ComponentActivity() {

    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()

    private var scope = lifecycleScope

    private lateinit var repository: Repository

    private lateinit var db: LocalDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = getDatabase(
            context = this.applicationContext
        )

        repository = Repository(
            serverHost = ServerHost.LOCAL_SABINA.url,
            db = db,
            deviceID = KeyValueStorage(this.applicationContext).getOrCreateDeviceId()
        )

        setSystemBars()

        setContent {
            val appData by repository.repositoryUiState.collectAsState()
            val navController: NavHostController = rememberNavController()

            Log.i("repositoryUiState:isLoading", appData.isLoading.toString())
            Log.i("repositoryUiState:error", appData.isLoading.toString())
            Log.i("repositoryUiState:DeviceID", appData.deviceID)

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

    override fun onStart() {
        super.onStart()
        scope.launch(Dispatchers.IO) {
            repository.syncRemoteTimer().onSuccess { body ->
                Log.i("MainActivity", "Sync Successfully $body")

                val requestBodySizeKB = body.length
                Log.i("MainActivity","Request Body Size (KB): $requestBodySizeKB")
            }.onError { error ->
                Log.i("MainActivity", "Sync Failed ${error.name}")
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
