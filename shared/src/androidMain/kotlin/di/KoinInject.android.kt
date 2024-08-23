package di

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module

actual class KoinInject(
    private val context: Context,
) {

    actual fun init() {
        try {
            startKoin {
                androidContext(context)
                androidLogger()
                modules(
                    repositoriesModule,
                    viewModelModule,
                )
            }
        } catch (e: Exception) {
            // Handle Koin initialization errors
            Log.e("KoinInject", "Error initializing Koin", e)
        }
    }


}