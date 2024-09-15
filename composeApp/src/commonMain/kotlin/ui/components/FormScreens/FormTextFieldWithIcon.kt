package ui.components.FormScreens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_eye
import bauchglueck.composeapp.generated.resources.ic_eye_slash
import bauchglueck.composeapp.generated.resources.ic_lock_fill
import bauchglueck.composeapp.generated.resources.ic_pills_fill
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

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
    leadingIcon: DrawableResource = Res.drawable.ic_pills_fill,
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
                imageVector = vectorResource(resource = leadingIcon),
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


@Composable
fun FormPasswordTextFieldWithIcon(
    modifier: Modifier = Modifier,
    leadingIcon: DrawableResource = Res.drawable.ic_pills_fill,
    visualTransformation: PasswordVisualTransformation =  PasswordVisualTransformation(),
    inputValue: String,
    onValueChange: (String) -> Unit,
) {
    var showPassword by remember { mutableStateOf(value = false) }

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
                imageVector = vectorResource(resource = Res.drawable.ic_lock_fill),
                contentDescription = "Localized description"
            )
        },
        trailingIcon = {
            if (showPassword) {
                IconButton(onClick = { showPassword = false }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_eye),
                        contentDescription = "hide_password"
                    )
                }
            } else {
                IconButton(
                    onClick = { showPassword = true }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_eye_slash),
                        contentDescription = "hide_password"
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        value = inputValue,
        colors = colors,
        onValueChange = {
            onValueChange(it)
        },
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}