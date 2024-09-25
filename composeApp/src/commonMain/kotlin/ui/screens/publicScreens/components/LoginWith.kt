package ui.screens.publicScreens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.screens.publicScreens.model.SignInProvider
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.clickableWithRipple
import ui.components.theme.sectionShadow
import ui.components.theme.text.FooterText

@Composable
fun LoginWith(
    provider: SignInProvider,
    onClick: () -> Unit = {}
) {
    val name = when(provider) {
        SignInProvider.Google -> SignInProvider.Google.name
        SignInProvider.Apple -> SignInProvider.Apple.name
    }

    val icon = when(provider) {
        SignInProvider.Google -> SignInProvider.Google.icon
        SignInProvider.Apple -> SignInProvider.Apple.icon
    }

    val text = "SignIn with $name"

    Row(
        modifier = Modifier
            .clickableWithRipple { onClick() }
            .sectionShadow()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = vectorResource(icon),
            contentDescription = text
        )
        FooterText(text = text)
    }
}