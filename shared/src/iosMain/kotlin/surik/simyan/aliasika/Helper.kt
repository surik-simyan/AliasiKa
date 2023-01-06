package surik.simyan.aliasika

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import surik.simyan.aliasika.di.appModule

class GameViewModelHelper : KoinComponent {
    private val greeting: GameViewModel by inject()
    fun getViewModel(): GameViewModel = GameViewModel()
}

fun initKoin() {
    startKoin {
        modules(appModule())
    }
}
