package di

import data.Repository
import data.local.LocalDatabase
import data.local.getDatabaseiOS
import data.network.ServerHost
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import util.KeyValueStorage
import viewModel.TimerViewModel

actual val platformModule = module {
    single<LocalDatabase> { getDatabaseiOS() }
    single<Repository> {
        Repository(
            serverHost = ServerHost.LOCAL_SABINA.url,
            db = get(),
            deviceID = KeyValueStorage().getOrCreateDeviceId()
        )
    }

    singleOf(::TimerViewModel)
}