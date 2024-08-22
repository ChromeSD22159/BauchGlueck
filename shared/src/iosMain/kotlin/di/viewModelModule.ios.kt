package di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import viewModel.TimerViewModel

actual val viewModelModule = module {
    singleOf(::TimerViewModel)
}