package surik.simyan.aliasika.android.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.github.theapache64.twyper.SwipedOutDirection
import com.github.theapache64.twyper.Twyper
import com.github.theapache64.twyper.rememberTwyperController
import dev.icerock.moko.mvvm.flow.compose.observeAsActions
import surik.simyan.aliasika.GameViewModel
import surik.simyan.aliasika.SharedStrings
import surik.simyan.aliasika.presentation.RightButton
import surik.simyan.aliasika.presentation.WrongButton

@Composable
fun GameSwipeScreen(navController: NavHostController, viewModel: GameViewModel) {
    val context = LocalContext.current

    val playingTeamName: String
    val playingTeamScore: State<Int>
    var showAlertDialog by remember { mutableStateOf(false) }
    val twyperController = rememberTwyperController()

    if (viewModel.playingTeam == GameViewModel.PlayingTeam.TeamOne) {
        playingTeamName = viewModel.teamOneName
        playingTeamScore = viewModel.teamOneScore.collectAsState()
    } else {
        playingTeamName = viewModel.teamTwoName
        playingTeamScore = viewModel.teamTwoScore.collectAsState()
    }
    val remainingTime by viewModel.remainingTime.collectAsState()
    val words by viewModel.words.collectAsState()

    val wordSwipe: (String, SwipedOutDirection) -> Unit = { _, direction ->
        if (direction == SwipedOutDirection.RIGHT) {
            viewModel.wordGuessed()
        } else {
            viewModel.wordUnguessed()
        }
    }

    viewModel.actions.observeAsActions { action ->
        if (action is GameViewModel.Action.RoundFinished) {
            viewModel.rotateWords()
            navController.navigateUp()
        }
    }

    BackHandler {
        viewModel.pauseTimer()
        showAlertDialog = true
    }
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = {
                showAlertDialog = false
                viewModel.resumeTimer()
            },
            confirmButton = {
                TextButton(onClick = {
                    showAlertDialog = false
                    viewModel.finishRoundEarly()
                }) { Text(SharedStrings.gameFinishPositive.toString(context)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAlertDialog = false
                    viewModel.resumeTimer()
                }) { Text(SharedStrings.gameFinishNegative.toString(context)) }
            },
            title = { Text(SharedStrings.gameFinishRoundTitle.toString(context)) },
            text = { Text(SharedStrings.gameFinishRoundMessage.toString(context)) }
        )
    }

    Surface(color = MaterialTheme.colors.secondary) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                backgroundColor = Color.White,
                shape = MaterialTheme.shapes.medium
            ) {
                Row {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(0.dp, 16.dp, 0.dp, 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            playingTeamName,
                            fontWeight = FontWeight.Normal,
                            fontSize = 24.sp
                        )
                        Text(
                            playingTeamScore.value.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(0.dp, 16.dp, 0.dp, 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            SharedStrings.gameTime.toString(context),
                            fontWeight = FontWeight.Normal,
                            fontSize = 24.sp
                        )
                        Text(
                            remainingTime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Twyper(
                items = words,
                twyperController = twyperController,
                onItemRemoved = wordSwipe,
                onEmpty = {
                    println("End reached")
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { item ->
                SwipeWord(item)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        twyperController.swipeLeft()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(WrongButton)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        SharedStrings.gameWrong.toString(context),
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        twyperController.swipeRight()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(RightButton)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        SharedStrings.gameCorrect.toString(context),
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeWord(text: String) {
    Card(
        backgroundColor = Color.White,
        elevation = 8.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}
