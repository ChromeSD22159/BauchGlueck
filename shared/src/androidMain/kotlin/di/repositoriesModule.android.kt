package di

import data.repositories.MedicationRepository
import data.Repository
import data.repositories.WeightRepository
import data.repositories.WaterIntakeRepository
import data.local.LocalDatabase
import data.local.getDatabase
import data.repositories.CountdownTimerRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import util.KeyValueStorage

actual val repositoriesModule = module {
    single<LocalDatabase> { getDatabase(androidContext()) }
    single<KeyValueStorage> { KeyValueStorage(androidContext()) }

    single<Repository> { Repository(get(), get(), get(), get())  }
    single { CountdownTimerRepository( get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
    single<MedicationRepository> { MedicationRepository(get()) }
    single<WaterIntakeRepository> { WaterIntakeRepository(get()) }
    single<WeightRepository> { WeightRepository(get()) }
}