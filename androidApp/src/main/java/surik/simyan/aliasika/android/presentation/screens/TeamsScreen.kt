package surik.simyan.aliasika.android.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.koin.androidx.compose.get
import surik.simyan.aliasika.presentation.MainViewModel
import surik.simyan.aliasika.SharedStrings

@Composable
fun TeamsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = get<MainViewModel>()
    var teamOne by remember { mutableStateOf("") }
    var teamTwo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.Group,
            "",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            SharedStrings.playTeams.toString(context),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = teamOne,
            onValueChange = { teamOne = it },
            label = { Text(SharedStrings.playTeamOne.toString(context)) },
            textStyle = TextStyle(
                color = MaterialTheme.colors.onBackground
            )
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = teamTwo,
            onValueChange = { teamTwo = it },
            label = { Text(SharedStrings.playTeamTwo.toString(context)) },
            textStyle = TextStyle(
                color = MaterialTheme.colors.onBackground
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (teamOne.trim().isNotEmpty()) {
                    viewModel.teamOneName = teamOne
                } else {
                    viewModel.teamOneName = SharedStrings.playTeamOne.toString(context)
                }
                if (teamTwo.trim().isNotEmpty()) {
                    viewModel.teamTwoName = teamTwo
                } else {
                    viewModel.teamTwoName = SharedStrings.playTeamTwo.toString(context)
                }
                navController.navigate("score")
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                SharedStrings.playStartGame.toString(context),
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
