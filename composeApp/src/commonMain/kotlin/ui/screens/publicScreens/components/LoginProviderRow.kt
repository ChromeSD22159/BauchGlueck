package ui.screens.publicScreens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.screens.publicScreens.model.SignInProvider
import ui.components.theme.text.BodyText

@Composable
fun LoginProviderRow(
    onContinueWithGoogle: () -> Unit = {},
    onContinueWithApple: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BodyText(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "--    oder    --",
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LoginWith(SignInProvider.Google) { onContinueWithGoogle() }

            LoginWith(SignInProvider.Apple) { onContinueWithApple() }
        }
    }
}