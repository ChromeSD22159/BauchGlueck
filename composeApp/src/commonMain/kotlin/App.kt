
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ui.navigations.NavGraph
import ui.theme.AppTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(
    makeToast: (String) -> Unit
) {
    AppTheme {
        SetSystemBars()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController: NavHostController = rememberNavController()

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                NavGraph(
                    navController,
                    makeToast = makeToast
                )
            }
        }
    }
}

@Composable
fun SetSystemBars() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    // Setze die Farben der Statusleiste
    systemUiController.setSystemBarsColor(
        color = MaterialTheme.colorScheme.background.copy(0f), // Transparente Farbe für Statusleiste
        darkIcons = useDarkIcons // Dunkle Icons für Statusleiste, wenn es kein Dunkelmodus ist
    )

    // Setze die Farbe der Navigationsleiste
    systemUiController.setNavigationBarColor(
        color = MaterialTheme.colorScheme.background.copy(0f), // Transparente Farbe für Navigationsleiste
        darkIcons = useDarkIcons // Dunkle Icons für Navigationsleiste, wenn es kein Dunkelmodus ist
    )
}