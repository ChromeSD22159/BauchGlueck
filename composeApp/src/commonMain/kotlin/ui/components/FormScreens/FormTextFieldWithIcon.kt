package ui.components.FormScreens

import android.service.autofill.OnClickAction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.text.input.ImeAction
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
import ui.components.theme.sectionShadow

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
    keyboardActions: KeyboardActions = KeyboardActions(),
    inputValue: String,
    onValueChange: (String) -> Unit,
) {
    val colors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        focusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 1f),

        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedContainerColor = MaterialTheme.colorScheme.surface,

        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    )

    TextField(
        leadingIcon = {
            Icon(
                imageVector = vectorResource(resource = leadingIcon),
                contentDescription = "Localized description",
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .sectionShadow()
            .clip(RoundedCornerShape(12.dp)),
        value = inputValue,
        colors = colors,
        onValueChange = {
            onValueChange(it)
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )


}


@Composable
fun FormPasswordTextFieldWithIcon(
    modifier: Modifier = Modifier,
    leadingIcon: DrawableResource = Res.drawable.ic_pills_fill,
    visualTransformation: PasswordVisualTransformation =  PasswordVisualTransformation(),
    inputValue: String,
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    onValueChange: (String) -> Unit,
) {
    var showPassword by remember { mutableStateOf(value = false) }

    val colors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        focusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 1f),

        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedContainerColor = MaterialTheme.colorScheme.surface,

        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    )

    TextField(
        leadingIcon = {
            Icon(
                imageVector = vectorResource(resource = Res.drawable.ic_lock_fill),
                contentDescription = "Localized description",
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
        trailingIcon = {
            if (showPassword) {
                IconButton(onClick = { showPassword = false }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_eye),
                        contentDescription = "hide_password",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                IconButton(
                    onClick = { showPassword = true }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_eye_slash),
                        contentDescription = "hide_password",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .sectionShadow()
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
        keyboardOptions = keyboardOptions.copy(
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = keyboardActions
    )
}

@Composable
fun FormTextFieldWithoutIcons(
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    keyboardActions: KeyboardActions = KeyboardActions(),
    inputValue: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE
) {
    val colors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        focusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 1f),

        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedContainerColor = MaterialTheme.colorScheme.surface,

        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    )

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .sectionShadow()
            .clip(RoundedCornerShape(12.dp)),
        value = inputValue,
        colors = colors,
        onValueChange = {
            onValueChange(it)
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        minLines = minLines,
        maxLines = maxLines
    )
}

@Composable
fun FormTextFieldWithIconAndDeleteButton(
    modifier: Modifier = Modifier,
    inputValue: String,
    icon: DrawableResource = Res.drawable.ic_lock_fill,
    onValueChange: (String) -> Unit,
    onClickAction: (String) -> Unit = {},
) {
    val colors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
        focusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 1f),

        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedContainerColor = MaterialTheme.colorScheme.surface,

        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    )

    TextField(
        leadingIcon = {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = vectorResource(resource = icon),
                contentDescription = "Localized description",
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
        trailingIcon = {
            if (inputValue.isNotEmpty()) {
                IconButton(
                    modifier = Modifier,
                    onClick = { onClickAction("") }) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Default.Clear,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Reset"
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .sectionShadow()
            .clip(RoundedCornerShape(12.dp)),
        value = inputValue,
        colors = colors,
        onValueChange = {
            onValueChange(it)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}
