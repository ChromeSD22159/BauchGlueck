package de.frederikkohler.bauchglueck

import android.content.ContentUris
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import de.frederikkohler.bauchglueck.ui.views.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.views.LoginView
import dev.icerock.moko.mvvm.flow.cStateFlow
import io.ktor.client.HttpClient
import network.createHttpClient
import viewModels.SharedRecipeViewModel
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import network.BauchGlueckClient
import util.onSuccess


class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val database = Firebase.database.reference.child("onlineUsers")
    private val viewModel: FirebaseAuthViewModel by viewModels()
    private val sharedRecipeViewModel: SharedRecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics

        sharedRecipeViewModel.fetchMeasureUnits(createHttpClient(OkHttp.create()))
        sharedRecipeViewModel.fetchRecipeCategories(createHttpClient(OkHttp.create()))

        setSystemBars()

        setContent {

            AppTheme {
                //App()

                LoginView()

            }
        }
    }

    private fun setSystemBars() {
        // Set decor to draw behind system bars (status bar)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set the status bar to transparent
        window.statusBarColor = Color.TRANSPARENT

        // Adjust the appearance of status bar icons and text
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        when (applicationContext.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                // Dark mode: Light text and icons on status bar
                controller.isAppearanceLightStatusBars = false
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                // Light mode: Dark text and icons on status bar
                controller.isAppearanceLightStatusBars = true
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                // Undefined mode: Use default setting (optional)
                controller.isAppearanceLightStatusBars = false // or true based on preference
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userStatusRef = database.child(currentUser.uid)

        userStatusRef.setValue(true)
        userStatusRef.onDisconnect().removeValue()
    }

    override fun onStop() {
        super.onStop()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.child(userId).removeValue() // Status auf "offline" setzen
    }
}

@Preview
@Composable
fun AppAndroidLightPreview() {
    AppTheme {
        LoginView()
    }
}
