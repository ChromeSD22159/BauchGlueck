package di

import data.repositories.MedicationRepository
import data.Repository
import data.repositories.WeightRepository
import data.repositories.WaterIntakeRepository
import data.local.LocalDatabase
import data.local.getDatabase
import data.repositories.CountdownTimerRepository
import data.repositories.MealPlanRepository
import data.repositories.MealRepository
import data.repositories.NoteRepository
import data.repositories.ShoppingListRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import util.KeyValueStorage


actual val repositoriesModule = module {
    single<LocalDatabase> { getDatabase(androidContext()) }
    single<KeyValueStorage> { KeyValueStorage(androidContext()) }

    single<Repository> { Repository(get(), get(), get(), get(), get(), get(), get(), get())  }
    single { CountdownTimerRepository( get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
    single { MedicationRepository( get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
    single { WaterIntakeRepository( get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
    single { WeightRepository( get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
    single { MealRepository( get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
    single { MealPlanRepository( get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
    single { NoteRepository(get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() ) }
    single { ShoppingListRepository(get(), serverHost = serverHost, deviceID = KeyValueStorage(androidContext()).getOrCreateDeviceId() )  }
}

