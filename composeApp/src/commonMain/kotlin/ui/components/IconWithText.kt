package ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IconWithText(
    rowModifier: Modifier = Modifier,
    textSize: Float,
    image: Int,
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
            imageVector = ImageVector.vectorResource(image),
            contentDescription = "$image"
        )

        TextElement(
            text = text,
            component = TextComponent.Small,
            fontSize = (textSize - 2).sp
        )
    }
}