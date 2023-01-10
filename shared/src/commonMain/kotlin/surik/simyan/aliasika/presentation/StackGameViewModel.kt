package surik.simyan.aliasika.presentation

import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import surik.simyan.aliasika.data.TeamsRepository
import surik.simyan.aliasika.data.WordsRepository

class StackGameViewModel(
    playingTime: Float,
    wordsRepository: WordsRepository,
    teamsRepository: TeamsRepository
) :
    AbstractGameViewModel(playingTime, wordsRepository, teamsRepository) {

    init {
        updateStackWords()
        startTimer()
    }

    private val _stackWords: CMutableStateFlow<List<String>> =
        MutableStateFlow(listOf<String>()).cMutableStateFlow()
    val stackWords: CStateFlow<List<String>> = _stackWords.cStateFlow()

    private fun updateStackWords() {
        viewModelScope.launch {
            _stackWords.emit(words.take(STACK_GAMEMODE_WORD_COUNT))
        }
    }

    override fun rotateWords(index: Int?) {
        if (index == null) {
            repeat(STACK_GAMEMODE_WORD_COUNT) {
                words.add(words.removeAt(0))
            }
        } else {
            words.add(words.removeAt(index))
        }
        updateStackWords()
    }

    override fun rotateRepoWords() {
        wordsRepository.rotateWords(STACK_GAMEMODE_WORD_COUNT)
    }

    override fun wordGuessed(index: Int?) {
        addPoint()
        rotateWords(index)
    }

    override fun wordUnguessed() = Unit // You can not unguess word in Stack gamemode
}
