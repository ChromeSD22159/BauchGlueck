package de.frederikkohler.bauchglueck.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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