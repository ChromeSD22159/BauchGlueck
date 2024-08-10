package de.frederikkohler.bauchglueck.ui.screens.authScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.RoundImageButton
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme

@Composable
fun HomeView(
    firebaseAuthViewModel: FirebaseAuthViewModel = viewModel()
) {
    var timer = firebaseAuthViewModel.timers.observeAsState()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20Refactor.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            RoundImageButton(R.drawable.icon_gear) {
                // open settingsheet
            }
        }




       timer.value?.forEach {
           Spacer(modifier = Modifier.height(16.dp))
           Column {
               Text(it.name)
               Text(it.duration.toString())
               Text(it.startDate.toString())
               Text(it.endDate.toString())
           }
       }

    }

}

@Preview
@Composable
fun HomePreview(){
    AppTheme {
        HomeView()
    }
}