package de.frederikkohler.bauchglueck.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.remote.GeminiApiClient
import kotlinx.coroutines.launch

@Composable
fun GenerateRecipeWithGemini(
    api: GeminiApiClient = GeminiApiClient()
) {

    val appendText = "Das Objekt soll folgende Felder enthalten: name, description, isSnack, isPrivate, isDeleted, preparation, preparationTimeInMinutes, ingredients (als Array von Objekten mit den Feldern id, name, amount, unit), protein, fat, sugar, kcal. Beispiel:{\\\\n  \\\\\\\"name\\\\\\\": \\\\\\\"Eiskalter Hibiskusblütentee, mit frischer Zitrone\\\\\\\",\\\\n  \\\\\\\"description\\\\\\\": \\\\\\\"Eiskalter Hibiskusblütentee, mit frischer Zitrone. Das Getränk schmeckt gut gekühlt am besten. Wer mag, kann es noch etwas süßen.\\\\\\\",\\\\n  \\\\\\\"isSnack\\\\\\\": false,\\\\n  \\\\\\\"isPrivate\\\\\\\": false,\\\\n  \\\\\\\"isDeleted\\\\\\\": false,\\\\n  \\\\\\\"preparation\\\\\\\": \\\\\\\"1. Die getrockneten Hibiskusblüten mit den Zitronenscheiben in ein sauberes Glasgefäß geben und mit 1 Liter kaltem Wasser übergießen. Das Gefäß gut verschließen und für mindestens 2 Stunden in den Kühlschrank stellen.\\\\\\\",\\\\n  \\\\\\\"preparationTimeInMinutes\\\\\\\": 10,\\\\n  \\\\\\\"ingredients\\\\\\\": [\\\\n    {\\\\n      \\\\\\\"id\\\\\\\": 1,\\\\n      \\\\\\\"name\\\\\\\": \\\\\\\"getrocknete Hibiskusblüten\\\\\\\",\\\\n      \\\\\\\"amount\\\\\\\": \\\\\\\"3\\\\\\\",\\\\n      \\\\\\\"unit\\\\\\\": \\\\\\\"TL\\\\\\\"\\\\n    },\\\\n    {\\\\n      \\\\\\\"id\\\\\\\": 2,\\\\n      \\\\\\\"name\\\\\\\": \\\\\\\"frische Bio-Zitrone\\\\\\\",\\\\n      \\\\\\\"amount\\\\\\\": \\\\\\\"3\\\\\\\",\\\\n      \\\\\\\"unit\\\\\\\": \\\\\\\"Scheiben\\\\\\\"\\\\n    },\\\\n    {\\\\n      \\\\\\\"id\\\\\\\": 3,\\\\n      \\\\\\\"name\\\\\\\": \\\\\\\"kaltes Wasser\\\\\\\",\\\\n      \\\\\\\"amount\\\\\\\": \\\\\\\"1\\\\\\\",\\\\n      \\\\\\\"unit\\\\\\\": \\\\\\\"L\\\\\\\"\\\\n    }\\\\n  ],\\\\n  \\\\\\\"protein\\\\\\\": 2.6,\\\\n  \\\\\\\"fat\\\\\\\": 14.6,\\\\n  \\\\\\\"sugar\\\\\\\": 10.5,\\\\n  \\\\\\\"kcal\\\\\\\": 262.5\\\\n}\\\"\\n\" +\n" + "\"}"

    val coroutineScope = rememberCoroutineScope()
    var prompt by remember {
        mutableStateOf("Gib mir ein JSON-Objekt zurück, das ein Rezept Idee oder Vorschlag für eine Person nach einem Magenbypass beschreibt. Es soll Proteein reich und Fettarm sein.")
    }
    var content by remember { mutableStateOf("") }
    var showProgress by remember { mutableStateOf(false) }

    Row {
        TextField(
            value = prompt,
            onValueChange = { prompt = it },
            modifier = Modifier.weight(7f)
        )
        TextButton(
            onClick = {
                if (prompt.isNotBlank()) {
                    coroutineScope.launch {
                        showProgress = true
                        content = api.generateContent(prompt + appendText).toString()
                        showProgress = false
                    }
                }
            },
            modifier = Modifier.weight(3f)
                .padding(all = 4.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text("Submit")
        }
    }

    Spacer(Modifier.height(16.dp))

    if (showProgress) {
        CircularProgressIndicator()
    } else {
        Text(content)
    }
}
