package surik.simyan.aliasika.presentation

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.format
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

    fun isGameEnded(): Boolean {
        Logger.d {
            "Team 1 points ${teamOneScore.value} Team 2 points ${teamTwoScore.value} Winning points $points"
        }
        if (teamOneScore.value >= points) {
            teamsRepository.winnerTeam = teamOneName
            Logger.d { "Team 1 won" }
            return true
        } else if (teamTwoScore.value >= points) {
            teamsRepository.winnerTeam = teamTwoName
            Logger.d { "Team 2 won" }
            return true
        }
        return false
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
}
