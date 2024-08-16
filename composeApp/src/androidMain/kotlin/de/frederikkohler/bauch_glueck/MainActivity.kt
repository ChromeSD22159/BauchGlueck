package de.frederikkohler.bauch_glueck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.frederikkohler.bauch_glueck.data.local.database.getDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val db = getDatabase(this)
            App(db)
        }
    }
}