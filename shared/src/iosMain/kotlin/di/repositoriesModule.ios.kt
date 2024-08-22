package di

import data.repositories.MedicationRepository
import data.Repository
import data.repositories.WeightRepository
import data.repositories.WaterIntakeRepository
import data.local.LocalDatabase
import data.local.getDatabaseiOS
import data.repositories.CountdownTimerRepository
import org.koin.dsl.module
import util.KeyValueStorage

actual val repositoriesModule = module {
    single<LocalDatabase> { getDatabaseiOS() }
    single<KeyValueStorage> { KeyValueStorage() }

    single<Repository> { Repository(get(), get(), get(), get() ) }
    single { CountdownTimerRepository(get(), serverHost, deviceID = KeyValueStorage().getOrCreateDeviceId() ) }
    single<MedicationRepository> { MedicationRepository(get()) }
    single<WaterIntakeRepository> { WaterIntakeRepository(get()) }
    single<WeightRepository> { WeightRepository(get()) }
}

