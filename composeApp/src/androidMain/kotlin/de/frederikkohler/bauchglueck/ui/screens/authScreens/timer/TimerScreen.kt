package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerScreen(navController: NavController) {
    BackScaffold(
        title = Screens.Timer.title,
        navController = navController
    ) {

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = { /*TODO*/ }) {
            Text(text = "Add Timer")
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = { /*TODO*/ }) {
            Text(text = "Add Timer")
        }


        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = { /*TODO*/ }) {
            Text(text = "Add Timer")
        }


        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = { /*TODO*/ }) {
            Text(text = "Add Timer")
        }

    }
}

