package ui.components.theme.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun FooterText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign = TextAlign.Start,
    weight: FontWeight = FontWeight.Normal,
    size: TextUnit = 11.sp,
    textDecoration: TextDecoration = TextDecoration.None
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        textAlign = textAlign,
        fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
        fontSize = size,
        fontWeight = weight,
        textDecoration = textDecoration
    )
}