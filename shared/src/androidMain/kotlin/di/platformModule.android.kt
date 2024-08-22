package di

import data.Repository
import data.local.LocalDatabase
import data.local.getDatabase
import data.network.ServerHost
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import util.KeyValueStorage
import viewModel.TimerViewModel

actual val platformModule = module {
    single<LocalDatabase> { getDatabase(androidContext()) }
    single<Repository> {
        Repository(
            serverHost = ServerHost.LOCAL_SABINA.url,
            db = get(),
            deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId()
        )
    }

    viewModelOf(::TimerViewModel)
}