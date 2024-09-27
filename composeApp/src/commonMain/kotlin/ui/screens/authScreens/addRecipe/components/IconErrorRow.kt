package ui.screens.authScreens.addRecipe.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@Composable
fun IconErrorRow(
    isError: Boolean = false,
    text: String = ""
) {
    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .alpha(if (isError) 1f else 0f),
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "",
            modifier = Modifier
        )

        Text(text)
    }
}