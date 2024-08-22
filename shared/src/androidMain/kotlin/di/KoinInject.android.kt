package di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

actual class KoinInject(
    val context: Context
) {

    actual fun init() {
        startKoin {
            androidContext(context)
            modules(
                platformModule,
                viewModelModule
            )
        }
    }
}