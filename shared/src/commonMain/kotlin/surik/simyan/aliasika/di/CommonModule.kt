package surik.simyan.aliasika.di

import org.koin.dsl.module
import surik.simyan.aliasika.data.TeamsRepository
import surik.simyan.aliasika.data.WordsRepository
import surik.simyan.aliasika.presentation.MainViewModel
import surik.simyan.aliasika.presentation.StackGameViewModel
import surik.simyan.aliasika.presentation.StandardGameViewModel
import surik.simyan.aliasika.presentation.SwipeGameViewModel

val commonModule = module {
    single<WordsRepository> {
        WordsRepository()
    }
    single<TeamsRepository> {
        TeamsRepository()
    }
    single<MainViewModel> {
        MainViewModel(get())
    }
    factory {
        StandardGameViewModel(get<MainViewModel>().time, get(), get())
    }
    factory {
        SwipeGameViewModel(get<MainViewModel>().time, get(), get())
    }
    factory {
        StackGameViewModel(get<MainViewModel>().time, get(), get())
    }
}
