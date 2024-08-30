package di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import viewModel.MedicationViewModel
import viewModel.SyncWorkerViewModel
import viewModel.TimerViewModel
import viewModel.WeightViewModel

actual val viewModelModule = module {
    viewModel { TimerViewModel(get()) }
    viewModel { WeightViewModel(get()) }
    viewModel { SyncWorkerViewModel(get()) }
    viewModel { MedicationViewModel(get()) }
}