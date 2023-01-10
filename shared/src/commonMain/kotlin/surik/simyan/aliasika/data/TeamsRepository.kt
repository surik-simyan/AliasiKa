package surik.simyan.aliasika.data

class TeamsRepository {
    private var playingTeam = PlayingTeam.TeamOne

    var teamOneName = ""
    var teamTwoName = ""
    var winnerTeam = ""

    var teamOneScore: Int = 0
    var teamTwoScore: Int = 0

    val playingTeamScore get() = if (playingTeam == PlayingTeam.TeamOne) teamOneScore else teamTwoScore
    val playingTeamName get() = if (playingTeam == PlayingTeam.TeamOne) teamOneName else teamTwoName

    private fun changeTeam() {
        playingTeam = if (playingTeam == PlayingTeam.TeamOne) PlayingTeam.TeamTwo else PlayingTeam.TeamOne
    }

    fun changePoints(value: Int) {
        if (playingTeam == PlayingTeam.TeamOne) {
            teamOneScore = value
        } else {
            teamTwoScore = value
        }
        changeTeam()
    }

    enum class PlayingTeam {
        TeamOne, TeamTwo
    }
}
