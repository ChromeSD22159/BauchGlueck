package ui.components.FormScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_pills_fill
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun FormTextFieldRow(
    rowModifier: Modifier = Modifier,
    modifierTextField: Modifier = Modifier,
    leadingIcon: DrawableResource = Res.drawable.ic_pills_fill,
    keyboardType: KeyboardType = KeyboardType.Text,
    inputValue: String,
    onValueChange: (String) -> Unit,
    displayText: String = "Dient zur bessern zu differenzierung.",
) {
    Column(
        modifier = rowModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        FormTextFieldWithIcon(
            modifier = modifierTextField,
            leadingIcon = leadingIcon,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            inputValue = inputValue,
        ) {
            onValueChange(it)
        }

        DescriptionText(displayText)
    }
}