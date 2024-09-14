package ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DropDownMenu(
    modifier: Modifier = Modifier,
    image: ImageVector = Icons.Outlined.MoreVert,
    dropDownOptions: List<DropdownMenuRow> = emptyList(),
) {
    var expanded by remember { mutableStateOf(false) } // Start with the menu closed

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        IconButton(
            modifier = modifier,
            onClick = { expanded = true },
            interactionSource = remember { MutableInteractionSource() },
        ) {
            Icon(Icons.Default.MoreVert, contentDescription = "Open dropdown menu")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            dropDownOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.text) },
                    onClick = {
                        expanded = false
                        option.onClick()
                    },
                    leadingIcon = {
                        option.leadingIcon?.let {
                            Icon(
                                it,
                                contentDescription = "Leading icon for ${option.text}"
                            )
                        }
                    },
                    trailingIcon = {
                        option.trailingIcon?.let {
                            Icon(
                                it,
                                contentDescription = "Trailing icon for ${option.text}"
                            )
                        }
                    }
                )
            }
        }
    }
}

data class DropdownMenuRow(
    val text: String,
    val onClick: () -> Unit,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null // Changed to ImageVector?
)

@Preview(
    showBackground = true,
)
@Composable
fun DropDownMenuPreview() {
    DropDownMenu(
        dropDownOptions = listOf(
            DropdownMenuRow(
                text = "Edit",
                onClick = { },
                leadingIcon = Icons.Outlined.Edit
            ),
            DropdownMenuRow(
                text = "Delete",
                onClick = { },
                leadingIcon = Icons.Outlined.Delete
            )
        )
    )
}