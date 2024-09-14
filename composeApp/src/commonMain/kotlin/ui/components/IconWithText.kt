package ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun IconWithText(
    rowModifier: Modifier = Modifier,
    textSize: Float,
    image: DrawableResource,
    text: String,
    gap: Dp = 4.dp
) {
    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(textSize.dp),
            imageVector = vectorResource(image),
            contentDescription = "$image"
        )

        TextElement(
            text = text,
            component = TextComponent.Small,
            fontSize = (textSize - 2).sp
        )
    }
}