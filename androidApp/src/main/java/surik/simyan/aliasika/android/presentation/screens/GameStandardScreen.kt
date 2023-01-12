package surik.simyan.aliasika.android.presentation.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.icerock.moko.mvvm.flow.compose.observeAsActions
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import surik.simyan.aliasika.SharedStrings
import surik.simyan.aliasika.presentation.*

@Composable
fun GameStandardScreen(
    navController: NavHostController,
    viewModel: StandardGameViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val playingTeamScore: State<Int> = viewModel.score.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()
    val words by viewModel.standardWords.collectAsState()
    var showAlertDialog by remember { mutableStateOf(false) }
    val wordsStates = rememberSaveable {
        listOf(
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false),
            mutableStateOf(false)
        )
    }

    val wordClick: (MutableState<Boolean>) -> Unit = {
        it.value = !it.value
        if (it.value) viewModel.wordGuessed() else viewModel.wordUnguessed()
    }

    viewModel.actions.observeAsActions { action ->
        when (action) {
            AbstractGameViewModel.Action.RoundFinished -> {
                navController.navigateUp()
            }
            AbstractGameViewModel.Action.FiveWordsGuessed -> {
                wordsStates.forEach {
                    it.value = false
                }
            }
            else -> Unit
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
            Card(
                modifier = Modifier.align(Alignment.Center),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Word(words[0], wordsStates[0], wordClick)
                    Word(words[1], wordsStates[1], wordClick)
                    Word(words[2], wordsStates[2], wordClick)
                    Word(words[3], wordsStates[3], wordClick)
                    Word(words[4], wordsStates[4], wordClick)
                }
            }
        }
    }
}

@Composable
fun Word(
    text: String,
    wordClicked: MutableState<Boolean>,
    onWordClick: (MutableState<Boolean>) -> Unit
) {
    Box(
        Modifier
            .background(color = if (wordClicked.value) Color(Guessed) else Color(Unguessed))
            .clickable {
                onWordClick(wordClicked)
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
