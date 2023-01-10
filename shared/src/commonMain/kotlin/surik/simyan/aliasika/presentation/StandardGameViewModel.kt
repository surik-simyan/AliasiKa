package surik.simyan.aliasika.presentation

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import surik.simyan.aliasika.data.TeamsRepository
import surik.simyan.aliasika.data.WordsRepository

class StandardGameViewModel(
    playingTime: Float,
    wordsRepository: WordsRepository,
    teamsRepository: TeamsRepository
) :
    AbstractGameViewModel(playingTime, wordsRepository, teamsRepository) {

    init {
//        updateStandardWords()
        viewModelScope.launch {
            _standardWords.emit(words.take(STANDARD_GAMEMODE_WORD_COUNT))
        }
        startTimer()
    }

    private val _standardWords: CMutableStateFlow<List<String>> =
        MutableStateFlow(listOf<String>()).cMutableStateFlow()
    val standardWords: CStateFlow<List<String>> = _standardWords.cStateFlow()

    private var guessedWordsCount = 0

    private fun updateStandardWords() {
        viewModelScope.launch {
            _standardWords.emit(words.take(STANDARD_GAMEMODE_WORD_COUNT))
            _actions.send(Action.FiveWordsGuessed).also {
                Logger.d { "FiveWordsGuessed" }
            }
        }
    }

    override fun rotateWords(index: Int?) {
        repeat(STANDARD_GAMEMODE_WORD_COUNT) {
            words.add(words.removeAt(0))
        }
        updateStandardWords()
    }

    override fun rotateRepoWords() {
        wordsRepository.rotateWords(STANDARD_GAMEMODE_WORD_COUNT)
    }

    override fun wordGuessed(index: Int?) {
        addPoint()
        if (guessedWordsCount == 4) {
            guessedWordsCount = 0
            rotateWords()
        } else {
            guessedWordsCount++
        }
    }

    override fun wordUnguessed() {
        minusPoint()
        guessedWordsCount--
    }
}
