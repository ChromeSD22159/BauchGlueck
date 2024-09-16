package ui.components.FormScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_minus
import bauchglueck.composeapp.generated.resources.ic_plus
import ui.components.theme.Section
import ui.components.theme.button.IconButton
import ui.components.theme.button.TextButton

@Composable
fun PlusMinusButtonForms(
    title: String? = null,
    displayColumn: @Composable () -> Unit,
    onPlus: () -> Unit,
    onMinus: () -> Unit
) {
    Section(title) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                resource = Res.drawable.ic_minus,
            ) { onMinus() }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                displayColumn()
            }

            IconButton(
                resource = Res.drawable.ic_plus,
            ) { onPlus() }
        }
    }
}