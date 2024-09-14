package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun IconListWithText(
    rowModifier: Modifier,
    textSize: Float = 14f,
    items: List<Pair<Int, String>>
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