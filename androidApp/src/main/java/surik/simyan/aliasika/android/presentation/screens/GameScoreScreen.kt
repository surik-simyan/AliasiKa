package surik.simyan.aliasika.android.presentation.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.koin.androidx.compose.get
import surik.simyan.aliasika.SharedStrings
import surik.simyan.aliasika.presentation.MainViewModel

@Composable
fun GameScoreScreen(navController: NavHostController, viewModel: MainViewModel = get()) {
    val context = LocalContext.current
    var showAlertDialog by remember { mutableStateOf(false) }

    val teamOneScore: State<Int> = viewModel.teamOneScore.collectAsState()
    val teamTwoScore: State<Int> = viewModel.teamTwoScore.collectAsState()
    val playingTeamName: State<String> = viewModel.playingTeamName.collectAsState()

    if (viewModel.isGameEnded()) {
        navController.navigate("winner") {
            popUpTo("home") {
                inclusive = true
            }
        }
    }

    BackHandler {
        if (teamOneScore.value > 0 || teamTwoScore.value > 0) {
            showAlertDialog = true
        } else {
            Log.d("BackScore", "Score navigateUp")
            navController.navigateUp()
        }
    }
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = {
                showAlertDialog = false
            },
            confirmButton = {
                TextButton(onClick = {
                    showAlertDialog = false
                    viewModel.endGameEarly()
                }) { Text(SharedStrings.gameFinishPositive.toString(context)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAlertDialog = false
                }) { Text(SharedStrings.gameFinishNegative.toString(context)) }
            },
            title = { Text(SharedStrings.gameFinishTitle.toString(context)) },
            text = { Text(SharedStrings.gameFinishMessage.toString(context)) }
        )
    }

    Surface(color = MaterialTheme.colors.secondary) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.align(Alignment.TopCenter),
                shape = MaterialTheme.shapes.medium
            ) {
                Row {
                    Column(
                        modifier = Modifier.weight(1f).padding(0.dp, 16.dp, 0.dp, 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            viewModel.teamOneName,
                            fontWeight = FontWeight.Normal,
                            fontSize = 24.sp
                        )
                        Text(
                            teamOneScore.value.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f).padding(0.dp, 16.dp, 0.dp, 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            viewModel.teamTwoName,
                            fontWeight = FontWeight.Normal,
                            fontSize = 24.sp
                        )
                        Text(
                            teamTwoScore.value.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    }
                }
            }
            Card(
                modifier = Modifier.align(Alignment.Center).clickable {
                    when (viewModel.gamemode) {
                        MainViewModel.Gamemode.STANDARD -> navController.navigate("standard")
                        MainViewModel.Gamemode.SWIPE -> navController.navigate("swipe")
                        MainViewModel.Gamemode.STACK -> navController.navigate("stack")
                    }
                },
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(0.dp, 64.dp, 0.dp, 64.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        playingTeamName.value,
                        fontWeight = FontWeight.Normal,
                        fontSize = 48.sp
                    )
                    Text(
                        SharedStrings.gameStart.toString(context),
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp
                    )
                }
            }
        }
    }
}
