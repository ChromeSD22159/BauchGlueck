package di

import data.repositories.MedicationRepository
import data.Repository
import data.repositories.WeightRepository
import data.repositories.WaterIntakeRepository
import data.local.LocalDatabase
import data.local.getDatabase
import data.repositories.CountdownTimerRepository
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import util.KeyValueStorage
import viewModel.TimerViewModel


actual val repositoriesModule = module {
    single<LocalDatabase> { getDatabase(androidContext()) }
    single<KeyValueStorage> { KeyValueStorage(androidContext()) }

    single<Repository> { Repository(get(), get(), get(), get())  }
    single { CountdownTimerRepository( get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
    single { MedicationRepository( get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
    single { WaterIntakeRepository( get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
    single { WeightRepository( get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
}