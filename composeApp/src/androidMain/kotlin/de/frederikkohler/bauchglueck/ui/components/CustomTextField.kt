package de.frederikkohler.bauchglueck.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun CustomTextField(label: String, variable: CMutableStateFlow<String>, onValueChange: (String) -> Unit) {
    val text by variable.collectAsStateWithLifecycle("")

    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text(label) },
        maxLines = 2,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        ),
    )
}

@Preview
@Composable
fun LoginViewPreview() {
    val mail: CMutableStateFlow<String> = MutableStateFlow("asdsd").cMutableStateFlow()

    AppTheme {
        CustomTextField("Label", mail) {
            mail.value = it
        }
    }
}