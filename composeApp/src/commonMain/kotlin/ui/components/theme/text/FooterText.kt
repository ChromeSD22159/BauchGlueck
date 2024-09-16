package ui.components.theme.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun FooterText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    weight: FontWeight = FontWeight.Normal,
    size: TextUnit = 11.sp
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
        fontSize = size,
        fontWeight = weight
    )
}