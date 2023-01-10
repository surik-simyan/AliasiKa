package surik.simyan.aliasika.android.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.icerock.moko.mvvm.flow.compose.observeAsActions
import org.koin.androidx.compose.get
import surik.simyan.aliasika.SharedStrings
import surik.simyan.aliasika.presentation.AbstractGameViewModel
import surik.simyan.aliasika.presentation.StackGameViewModel

@Composable
fun GameStackScreen(navController: NavHostController, viewModel: StackGameViewModel = get()) {
    val context = LocalContext.current
    val playingTeamScore: State<Int> = viewModel.score.collectAsState()
    var showAlertDialog by remember { mutableStateOf(false) }
    val lazyColumnState = rememberLazyListState()

    val remainingTime by viewModel.remainingTime.collectAsState()
    val words by viewModel.stackWords.collectAsState()

    val wordClick: (Int) -> Unit = { index ->
        viewModel.wordGuessed(index)
    }

    viewModel.actions.observeAsActions { action ->
        if (action is AbstractGameViewModel.Action.RoundFinished) {
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
                .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
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
                            viewModel.playingTeamName,
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
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                shape = MaterialTheme.shapes.medium.copy(
                    bottomStart = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                )
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    userScrollEnabled = false,
                    state = lazyColumnState
                ) {
                    itemsIndexed(items = words) { index, word ->
                        StackWord(word, onWordClick = {
                            wordClick(index)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun StackWord(text: String, onWordClick: () -> Unit) {
    Box(
        Modifier
            .clickable {
                onWordClick()
            }
    ) {
        Text(
            text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp
        )
    }
}
