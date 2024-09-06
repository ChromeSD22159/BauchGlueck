package di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import viewModel.MedicationViewModel
import viewModel.RecipeViewModel
import viewModel.SyncWorkerViewModel
import viewModel.TimerScreenViewModel
import viewModel.WeightScreenViewModel

actual val viewModelModule = module {
    viewModel { TimerScreenViewModel(get()) }
    viewModel { WeightScreenViewModel(get()) }
    viewModel { SyncWorkerViewModel(get()) }
    viewModel { MedicationViewModel(get()) }
    viewModel { RecipeViewModel(get()) }
}