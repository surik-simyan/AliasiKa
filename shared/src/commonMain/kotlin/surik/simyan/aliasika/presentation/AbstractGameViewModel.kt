package surik.simyan.aliasika.presentation

import dev.icerock.moko.mvvm.flow.*
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import surik.simyan.aliasika.data.TeamsRepository
import surik.simyan.aliasika.data.WordsRepository
import surik.simyan.aliasika.util.CoroutineTimer
import surik.simyan.aliasika.util.CoroutineTimerListener

abstract class AbstractGameViewModel(
    private val playingTime: Float,
    val wordsRepository: WordsRepository,
    private val teamsRepository: TeamsRepository
) : ViewModel() {
    val playingTeamName = teamsRepository.playingTeamName.value

    private val _score: CMutableStateFlow<Int> =
        MutableStateFlow(teamsRepository.playingTeamScore.value).cMutableStateFlow()
    val score: CStateFlow<Int> = _score.cStateFlow()

    val words: MutableList<String> = wordsRepository.words.toMutableList()

    private val _remainingTime: MutableStateFlow<String> = MutableStateFlow("")
    val remainingTime: CStateFlow<String> = _remainingTime.cStateFlow()

    protected val _actions = Channel<Action>(Channel.BUFFERED)
    val actions: CFlow<Action> get() = _actions.receiveAsFlow().cFlow()

    fun startTimer() {
        timer.startTimer(playingTime.toLong())
    }

    fun pauseTimer() {
        timer.pauseTimer()
    }

    fun resumeTimer() {
        timer.continueTimer()
    }

    private fun stopTimer() {
        timer.stopTimer()
    }

    private val timer = CoroutineTimer(object : CoroutineTimerListener {
        override fun onTick(timeLeft: Long?, error: Exception?) {
            _remainingTime.value = timeLeft.toString()
        }

        override fun onStop(error: Exception?) {
            super.onStop(error)
            timerFinished()
        }
    })

    private fun timerFinished() {
        rotateRepoWords()
        changeRepoPoints()
        viewModelScope.launch {
            _actions.send(Action.RoundFinished)
        }
    }

    private fun changeRepoPoints() {
        teamsRepository.changePoints(score.value)
    }

    fun finishRoundEarly() {
        resumeTimer()
        stopTimer()
    }

    sealed interface Action {
        object RoundFinished : Action
        object FiveWordsGuessed : Action
    }

    abstract fun rotateWords(index: Int? = null)
    abstract fun rotateRepoWords()
    abstract fun wordGuessed(index: Int? = null)
    abstract fun wordUnguessed()

    fun addPoint() {
        viewModelScope.launch {
            _score.emit(_score.value + 1)
        }
    }

    fun minusPoint() {
        viewModelScope.launch {
            _score.emit(_score.value - 1)
        }
    }

    companion object {
        const val STANDARD_GAMEMODE_WORD_COUNT = 5
        const val SWIPE_GAMEMODE_WORD_COUNT = 2
        const val STACK_GAMEMODE_WORD_COUNT =
            20 // THIS IS HARDCODE DUE THE FACT THAT I WAS UNABLE TO GET AMOUNT OF WORDS IN SWIFT UI
    }
}
