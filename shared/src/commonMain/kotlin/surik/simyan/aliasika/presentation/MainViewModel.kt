package surik.simyan.aliasika.presentation

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import surik.simyan.aliasika.data.TeamsRepository

class MainViewModel(private val teamsRepository: TeamsRepository) : ViewModel() {

    // Team one score
    var teamOneScore = teamsRepository.teamOneScore

    // Team two score
    var teamTwoScore = teamsRepository.teamTwoScore

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

    // PlayingTeam
    var playingTeam = TeamsRepository.PlayingTeam.TeamOne

    fun endGameEarly() {
        teamsRepository.winnerTeam = if (teamOneScore > teamTwoScore) {
            teamOneName
        } else {
            teamTwoName
        }
    }

    fun isGameEnded(): Boolean {
        if (playingTeam == TeamsRepository.PlayingTeam.TeamOne && teamOneScore >= points) {
            teamsRepository.winnerTeam = teamOneName
            return true
        } else if (playingTeam == TeamsRepository.PlayingTeam.TeamTwo && teamTwoScore >= points) {
            teamsRepository.winnerTeam = teamTwoName
            return true
        }
        return false
    }

    fun playingTeamName() = teamsRepository.playingTeamName

    fun playingTeamScore() = teamsRepository.playingTeamScore

    fun winnerTeamName() = teamsRepository.winnerTeam

    fun resetValues() {
        teamOneScore = 0
        teamTwoScore = 0
        gamemode = Gamemode.STANDARD
        points = 100F
        time = 45F
        teamOneName = ""
        teamTwoName = ""
        playingTeam = TeamsRepository.PlayingTeam.TeamOne
    }

    enum class Gamemode {
        STANDARD, SWIPE, STACK
    }
}
