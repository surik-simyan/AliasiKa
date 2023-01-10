package surik.simyan.aliasika.presentation

import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import surik.simyan.aliasika.data.TeamsRepository
import surik.simyan.aliasika.data.WordsRepository

class SwipeGameViewModel(
    playingTime: Float,
    wordsRepository: WordsRepository,
    teamsRepository: TeamsRepository
) :
    AbstractGameViewModel(playingTime, wordsRepository, teamsRepository) {

    init {
        updateSwipeWords()
        startTimer()
    }

    private val _swipeWords: CMutableStateFlow<List<String>> =
        MutableStateFlow(listOf<String>()).cMutableStateFlow()
    val swipeWords: CStateFlow<List<String>> = _swipeWords.cStateFlow()

    private fun updateSwipeWords() {
        viewModelScope.launch {
            _swipeWords.emit(words.take(SWIPE_GAMEMODE_WORD_COUNT + 1))
        }
    }

    override fun rotateWords(index: Int?) {
        repeat(SWIPE_GAMEMODE_WORD_COUNT) {
            words.add(words.removeAt(0))
        }
        updateSwipeWords()
    }

    override fun rotateRepoWords() {
        wordsRepository.rotateWords(SWIPE_GAMEMODE_WORD_COUNT + 1)
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
