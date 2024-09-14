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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.icon_stromach
import de.frederikkohler.bauchglueck.R
import org.jetbrains.compose.resources.vectorResource
import ui.theme.AppTheme
import utils.getDifferenceDateString

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InfoCard(
    firstnames: String,
    surgeryDateTimeStamp: Long
) {
    val context = LocalContext.current

    AppTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        ),
                    )
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
                Text(
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    text = context.getString(
                        R.string.settings_sheet_hallo_firstname,
                        firstnames
                    )
                )

                Text(
                    modifier = Modifier,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    text = context.getString(R.string.settings_sheet_unglaublich)
                )

                Text(
                    modifier = Modifier,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    text = getDifferenceDateString(surgeryDateTimeStamp)
                )
            }
        }
    }
}