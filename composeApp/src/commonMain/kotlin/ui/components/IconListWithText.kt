package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun IconListWithText(
    rowModifier: Modifier,
    textSize: Float = 14f,
    items: List<Pair<DrawableResource, String>>
) {
    items.forEach {
        IconWithText(
            rowModifier = rowModifier,
            textSize = textSize,
            image = it.first,
            text = it.second
        )
    }
}