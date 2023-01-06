package surik.simyan.aliasika.android.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import surik.simyan.aliasika.GameViewModel

val androidModule = module {
    viewModel {
        GameViewModel()
    }
}
