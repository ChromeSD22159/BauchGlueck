package de.frederikkohler.bauchglueck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.BackgroundBlobWithStomach
import de.frederikkohler.bauchglueck.ui.theme.AppTheme

@Composable
fun LaunchScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopEnd
    ) {
        BackgroundBlobWithStomach()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                text = stringResource(R.string.app_name),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LaunchScreenPreview() {
    AppTheme {
        LaunchScreen()
    }
}