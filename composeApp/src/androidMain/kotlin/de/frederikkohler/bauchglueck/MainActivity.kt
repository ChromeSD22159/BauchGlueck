package de.frederikkohler.bauchglueck

import App
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.permission.permissionUtil
import data.repositories.FirebaseRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import di.KoinInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import util.ApplicationContextHolder

class MainActivity : ComponentActivity() {

    private val firebaseRepository = FirebaseRepository()
    private lateinit var onlineUserCountJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.initialize(this)

        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_bauch_glueck_new,
                showPushNotification = true,
            )
        )

        val permissionUtil by permissionUtil()
        permissionUtil.askNotificationPermission()
    }

    @OptIn(ExperimentalCoilApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()

        setSystemBars()

        KoinInject(applicationContext).init()

        setContent {
            ApplicationContextHolder.context = applicationContext

            // TODO implement AsyncImageLoader on iOS
            setSingletonImageLoaderFactory { context ->
                getAsyncImageLoader(context)
            }

            App {
                Toast.makeText(applicationContext, "Keine Serververbindung", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            firebaseRepository.markUserOnline()

            firebaseRepository.getOnlineUserCount {
                logging().info {"Aktuelle Online-Benutzer: $it"}
            }
        }

        startObservingOnlineUserCount()
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            firebaseRepository.markUserOffline()
        }

        stopObservingOnlineUserCount()
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

    private fun startObservingOnlineUserCount() {
        onlineUserCountJob = firebaseRepository.observeOnlineUserCount { count ->
            logging().info {"Aktuelle Online-Benutzer: $count"}
        }
    }

    private fun stopObservingOnlineUserCount() {
        if (::onlineUserCountJob.isInitialized) {
            onlineUserCountJob.cancel()
        }
    }
}