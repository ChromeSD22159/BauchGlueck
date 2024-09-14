
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.koin.compose.currentKoinScope
import ui.navigations.NavGraph
import ui.theme.AppTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(
    makeToast: (String) -> Unit
) {
    AppTheme {

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
                ) {
                    makeToast(it)
                }
            }
        }
    }
}

@Composable
inline fun <reified T: ViewModel> koinViewModel(): T {
    val scope = currentKoinScope()
    return viewModel {
        scope.get<T>()
    }
}