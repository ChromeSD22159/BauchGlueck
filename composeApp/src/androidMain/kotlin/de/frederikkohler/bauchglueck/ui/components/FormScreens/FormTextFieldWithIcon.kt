package de.frederikkohler.bauchglueck.ui.components.FormScreens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.R

/**
 * A Composable function that displays a text field with a leading icon.
 *
 * @param modifier A [Modifier] for styling and configuring this TextField.
 * @param leadingIcon The resource ID of the leading icon to be displayed in the TextField.
 * @param keyboardOptions Options to configure the keyboard's behavior for this TextField.
 * @param inputValue The current text value of the TextField.
 * @param onValueChange A callback that is triggered when the text value changes.
 */

@Composable
fun FormTextFieldWithIcon(
    modifier: Modifier = Modifier,
    leadingIcon: Int = R.drawable.ic_pills_fill,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    inputValue: String,
    onValueChange: (String) -> Unit,
) {
    val colors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        focusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
        unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
        focusedContainerColor = Color.Gray.copy(alpha = 0.2f),
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    )

    TextField(
        leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = leadingIcon),
                contentDescription = "Localized description"
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        value = inputValue,
        colors = colors,
        onValueChange = {
            onValueChange(it)
        },
        keyboardOptions = keyboardOptions
    )
}