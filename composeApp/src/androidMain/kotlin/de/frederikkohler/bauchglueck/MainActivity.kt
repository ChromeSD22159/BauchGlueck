package de.frederikkohler.bauchglueck

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import de.frederikkohler.bauchglueck.ui.navigations.AuthNavigation
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.LoginView
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import viewModel.SharedRecipeViewModel

class MainActivity : ComponentActivity() {
    private val sharedRecipeViewModel: SharedRecipeViewModel by viewModels()
    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedRecipeViewModel.fetchMeasureUnits()
        sharedRecipeViewModel.fetchRecipeCategories()

        setSystemBars()

        setContent {
            AppTheme {

                AuthNavigation(firebaseAuthViewModel)

            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Refresh data when the app resumes
        // syncFirebase()
    }

    override fun onPause() {
        super.onPause()
        firebaseAuthViewModel.syncFirebase()
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
}

@Preview
@Composable
fun AppAndroidLightPreview() {
    AppTheme {
        LoginView({})
    }
}
