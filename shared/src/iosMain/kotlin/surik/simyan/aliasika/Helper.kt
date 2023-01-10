package surik.simyan.aliasika

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import surik.simyan.aliasika.di.appModule
import surik.simyan.aliasika.presentation.MainViewModel
import surik.simyan.aliasika.presentation.StackGameViewModel
import surik.simyan.aliasika.presentation.StandardGameViewModel
import surik.simyan.aliasika.presentation.SwipeGameViewModel

@Suppress("unused")
class MainViewModelHelper : KoinComponent {
    val mainViewModel: MainViewModel by inject()
}

@Suppress("unused")
class StandardGameViewModelHelper : KoinComponent {
    val standardGameViewModel: StandardGameViewModel by inject()
}

@Suppress("unused")
class SwipeGameViewModelHelper : KoinComponent {
    val swipeGameViewModel: SwipeGameViewModel by inject()
}

@Suppress("unused")
class StackGameViewModelHelper : KoinComponent {
    val stackGameViewModel: StackGameViewModel by inject()
}

@Suppress("unused")
fun initKoin() {
    startKoin {
        modules(appModule())
    }
}
