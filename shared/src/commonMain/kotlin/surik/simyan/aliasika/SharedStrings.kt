package surik.simyan.aliasika

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import surik.simyan.aliasika.moko.resources.shared.MR


object SharedStrings {
    val gameStart: StringDesc = MR.strings.game_start.desc()
    val gameTime: StringDesc = MR.strings.game_time.desc()
    val gameFinishRoundTitle: StringDesc = MR.strings.game_finish_round_title.desc()
    val gameFinishRoundMessage: StringDesc = MR.strings.game_finish_round_message.desc()
    val gameFinishPositive: StringDesc = MR.strings.game_finish_positive.desc()
    val gameFinishNegative: StringDesc = MR.strings.game_finish_negative.desc()
    val gameFinishTitle: StringDesc = MR.strings.game_finish_title.desc()
    val gameFinishMessage: StringDesc = MR.strings.game_finish_message.desc()
    val gameCorrect: StringDesc = MR.strings.game_correct.desc()
    val gameWrong: StringDesc = MR.strings.game_wrong.desc()
    val gamemodeStack: StringDesc = MR.strings.gamemode_stack.desc()
    val gamemodeStandard: StringDesc = MR.strings.gamemode_standard.desc()
    val gamemodeSwipe: StringDesc = MR.strings.gamemode_swipe.desc()
    val next: StringDesc = MR.strings.next.desc()
    val playTime: StringDesc = MR.strings.play_time.desc()
    val playStartGame: StringDesc = MR.strings.play_start_game.desc()
    val playTeamTwo: StringDesc = MR.strings.play_team_two.desc()
    val playTeamOne: StringDesc = MR.strings.play_team_one.desc()
    val playGamemode: StringDesc = MR.strings.play_gamemode.desc()
    val playPoints: StringDesc = MR.strings.play_points.desc()
    val playTeams: StringDesc = MR.strings.play_teams.desc()
    val play: StringDesc = MR.strings.wordset_play.desc()
}

fun gameEnd(input: String): StringDesc {
    return MR.strings.game_end.format(input)
}
