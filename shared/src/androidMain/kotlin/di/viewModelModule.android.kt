package di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import viewModel.TimerViewModel

actual val viewModelModule = module {
    viewModel {
        TimerViewModel(get())
    }
}