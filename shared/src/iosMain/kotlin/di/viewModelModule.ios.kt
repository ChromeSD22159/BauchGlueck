package di

import viewModel.SyncWorkerViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import viewModel.TimerScreenViewModel
import viewModel.WeightScreenViewModel
import viewModel.MedicationViewModel
import viewModel.RecipeViewModel
import viewModel.MealViewModel

actual val viewModelModule = module {
    singleOf(::TimerScreenViewModel)
    singleOf(::WeightScreenViewModel)
    singleOf(::SyncWorkerViewModel)
    singleOf(::MedicationViewModel)
    singleOf(::RecipeViewModel)
    singleOf(::MealViewModel)
}