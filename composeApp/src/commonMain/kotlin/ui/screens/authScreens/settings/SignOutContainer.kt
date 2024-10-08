package ui.screens.authScreens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.icon_stromach
import de.frederikkohler.bauchglueck.R
import org.jetbrains.compose.resources.vectorResource
import ui.theme.AppTheme


@Composable
fun SignOutContainer(
    onSignOut: () -> Unit
) {
    AppTheme {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    )
                ),
            colors = ButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            onClick = {
                onSignOut()
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    16.dp,
                    alignment = Alignment.Start
                )
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.icon_stromach),
                    contentDescription = "icon",
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                )

                Text(stringResource(R.string.settings_sheet_signout_button_text))
            }

        }


    }
}