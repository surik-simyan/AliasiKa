package surik.simyan.aliasika.presentation

import dev.icerock.moko.mvvm.flow.CFlow
import dev.icerock.moko.mvvm.flow.cFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import surik.simyan.aliasika.data.TeamsRepository
import surik.simyan.aliasika.moko.resources.shared.MR

class MainViewModel(private val teamsRepository: TeamsRepository) : ViewModel() {

    // Team one score
    var teamOneScore = teamsRepository.teamOneScore

    // Team two score
    var teamTwoScore = teamsRepository.teamTwoScore

    // Playing team name
    var playingTeamName = teamsRepository.playingTeamName

    // Gamemode
    var gamemode: Gamemode = Gamemode.STANDARD

    // Points to play
    var points = 100F

    // Time of one round
    var time = 45F

    // Team One Name
    var teamOneName
        get() = teamsRepository.teamOneName
        set(value) {
            teamsRepository.teamOneName = value
        }

    // Team Two Name
    var teamTwoName
        get() = teamsRepository.teamTwoName
        set(value) {
            teamsRepository.teamTwoName = value
        }

    fun endGameEarly() {
        teamsRepository.winnerTeam = if (teamOneScore.value > teamTwoScore.value) {
            teamOneName
        } else {
            teamTwoName
        }
    }

    private val _actions = Channel<Action>(Channel.BUFFERED)
    val actions: CFlow<Action> get() = _actions.receiveAsFlow().cFlow()

    fun isGameEnded() {
        if (teamOneScore.value >= points) {
            teamsRepository.winnerTeam = teamOneName
            viewModelScope.launch {
                _actions.send(Action.GameFinished)
            }
        } else if (teamTwoScore.value >= points) {
            teamsRepository.winnerTeam = teamTwoName
            viewModelScope.launch {
                _actions.send(Action.GameFinished)
            }
        }
    }

    fun winnerTeamName() = teamsRepository.winnerTeam

    fun gameWinnerText(): StringDesc {
        return MR.strings.game_end.format(winnerTeamName())
    }

    fun resetValues() {
        teamsRepository.resetValues()
        gamemode = Gamemode.STANDARD
        points = 100F
        time = 45F
    }

    enum class Gamemode {
        STANDARD, SWIPE, STACK
    }

    sealed interface Action {
        object GameFinished : Action
    }
}
