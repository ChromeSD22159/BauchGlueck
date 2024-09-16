package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

@Composable
fun Modifier.clickableWithRipple(
    onClick: () -> Unit
): Modifier {
    return this.clickable(
        role = Role.Button,
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}