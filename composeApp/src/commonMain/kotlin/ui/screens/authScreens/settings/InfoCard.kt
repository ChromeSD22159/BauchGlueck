package ui.screens.authScreens.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.icon_stromach
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.text.BodyText
import ui.components.theme.text.HeadlineText
import ui.theme.AppTheme
import utils.getDifferenceDateString

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InfoCard(
    firstnames: String,
    surgeryDateTimeStamp: Long
) {
    val brush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
    )

    AppTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(
                    brush = brush
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.icon_stromach),
                contentDescription = "icon",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
            )

            Column {

                HeadlineText(text = "Hallo, $firstnames", color = MaterialTheme.colorScheme.onPrimary)

                BodyText("Unglaublich, wie schnell die Zeit vergeht!", color = MaterialTheme.colorScheme.onPrimary)

                BodyText(getDifferenceDateString(surgeryDateTimeStamp), color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}