package di

import data.repositories.MedicationRepository
import data.Repository
import data.repositories.WeightRepository
import data.repositories.WaterIntakeRepository
import data.repositories.MealRepository
import data.local.LocalDatabase
import data.local.getDatabaseiOS
import data.repositories.CountdownTimerRepository
import data.repositories.MealPlanRepository
import data.repositories.NoteRepository
import data.repositories.ShoppingListRepository
import org.koin.dsl.module
import util.KeyValueStorage

actual val repositoriesModule = module {
    single<LocalDatabase> { getDatabaseiOS() }
    single<KeyValueStorage> { KeyValueStorage() }

    single<Repository> { Repository(get(), get(), get(), get(), get(), get(), get(), get() ) }
    single { CountdownTimerRepository(get(), serverHost, deviceID = KeyValueStorage().getOrCreateDeviceId() ) }
    single { MedicationRepository(get(), serverHost, deviceID = KeyValueStorage().getOrCreateDeviceId() ) }
    single { WaterIntakeRepository(get(), serverHost, deviceID = KeyValueStorage().getOrCreateDeviceId() ) }
    single { WeightRepository(get(), serverHost, deviceID = KeyValueStorage().getOrCreateDeviceId() ) }
    single { MealRepository(get(), serverHost, deviceID = KeyValueStorage().getOrCreateDeviceId() ) }
    single { MealPlanRepository(get(), serverHost, deviceID = KeyValueStorage().getOrCreateDeviceId() ) }
    single { NoteRepository(get(), serverHost, deviceID = KeyValueStorage().getOrCreateDeviceId() ) }
    single { ShoppingListRepository(get(), serverHost = serverHost, deviceID = KeyValueStorage().getOrCreateDeviceId())  }
}

