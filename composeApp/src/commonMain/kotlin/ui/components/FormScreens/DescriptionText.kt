package ui.components.FormScreens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DescriptionText(
    displayText: String
) {
    Text(
        text = displayText,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        fontSize = MaterialTheme.typography.bodySmall.fontSize
    )
}