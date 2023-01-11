package surik.simyan.aliasika.presentation

import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import surik.simyan.aliasika.data.TeamsRepository
import surik.simyan.aliasika.data.WordsRepository

class SwipeGameViewModel(
    playingTime: Float,
    wordsRepository: WordsRepository,
    teamsRepository: TeamsRepository
) :
    AbstractGameViewModel(playingTime, wordsRepository, teamsRepository) {

    private val _swipeWords: CMutableStateFlow<List<String>> =
        MutableStateFlow(listOf<String>()).cMutableStateFlow()
    val swipeWords: CStateFlow<List<String>> = _swipeWords.cStateFlow()

    init {
        updateSwipeWords()
        startTimer()
    }

    private fun updateSwipeWords() {
        _swipeWords.value = words.take(SWIPE_GAMEMODE_WORD_COUNT)
    }

    override fun rotateWords(index: Int?) {
        words.add(words.removeAt(0))
        updateSwipeWords()
    }

    override fun rotateRepoWords() {
        wordsRepository.rotateWords(SWIPE_GAMEMODE_WORD_COUNT)
    }

    override fun wordGuessed(index: Int?) {
        addPoint()
        rotateWords()
    }

    override fun wordUnguessed() {
        minusPoint()
        rotateWords()
    }
}
