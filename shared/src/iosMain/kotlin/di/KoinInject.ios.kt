package di

import org.koin.core.context.startKoin

actual class KoinInject {

    actual fun init() {
        startKoin {
            modules(platformModule)
        }
    }
}