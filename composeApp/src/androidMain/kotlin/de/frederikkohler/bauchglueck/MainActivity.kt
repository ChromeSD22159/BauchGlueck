package de.frederikkohler.bauchglueck

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import de.frederikkohler.bauchglueck.ui.theme.BkTheme


class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val database = Firebase.database.reference.child("onlineUsers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = Firebase.analytics

        setContent {
            BkTheme {
                App()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val userStatusRef = database.child(currentUser.uid) // Correct reference chaining

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
    BkTheme {
        App()
    }
}