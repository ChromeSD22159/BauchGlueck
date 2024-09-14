package de.frederikkohler.bauchglueck

import App
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import di.KoinInject

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalCoilApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart( ) {
        super.onStart( )

        KoinInject(applicationContext).init()

        setSystemBars()

        setContent {
            // TODO implement AsyncImageLoader on iOS
            setSingletonImageLoaderFactory { context ->
                getAsyncImageLoader(context)
            }

            App {
                Toast.makeText(applicationContext, "Keine Serververbindung", Toast.LENGTH_SHORT).show()
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

fun getAsyncImageLoader(context: PlatformContext) = ImageLoader.Builder(context).crossfade(true).logger(
    DebugLogger()
).build()