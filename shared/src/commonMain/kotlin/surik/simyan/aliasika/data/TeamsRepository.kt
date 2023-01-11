package surik.simyan.aliasika.data

import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import kotlinx.coroutines.flow.MutableStateFlow

class TeamsRepository {

    var playingTeam = PlayingTeam.TeamOne
    var teamOneName = ""
        set(value) {
            _playingTeamName.value = value
            field = value
        }
    var teamTwoName = ""
    var winnerTeam = ""

    private val _teamOneScore: CMutableStateFlow<Int> =
        MutableStateFlow(0).cMutableStateFlow()
    val teamOneScore: CStateFlow<Int> = _teamOneScore.cStateFlow()

    private val _teamTwoScore: CMutableStateFlow<Int> =
        MutableStateFlow(0).cMutableStateFlow()
    val teamTwoScore: CStateFlow<Int> = _teamTwoScore.cStateFlow()

    private val _playingTeamName: CMutableStateFlow<String> =
        MutableStateFlow(teamOneName).cMutableStateFlow()
    val playingTeamName: CStateFlow<String> = _playingTeamName.cStateFlow()

    val playingTeamScore get() = if (playingTeam == PlayingTeam.TeamOne) teamOneScore else teamTwoScore

    private fun changeTeam() {
        playingTeam =
            if (playingTeam == PlayingTeam.TeamOne) PlayingTeam.TeamTwo else PlayingTeam.TeamOne
    }

    fun changePoints(value: Int) {
        if (playingTeam == PlayingTeam.TeamOne) {
            _teamOneScore.value = value
        } else {
            _teamTwoScore.value = value
        }
        changeTeam()
    }

    fun resetValues() {
        _teamOneScore.value = 0
        _teamTwoScore.value = 0
        _playingTeamName.value = ""
        playingTeam = PlayingTeam.TeamOne
        winnerTeam = ""
    }

    enum class PlayingTeam {
        TeamOne, TeamTwo
    }
}
