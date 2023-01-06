package surik.simyan.aliasika.android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import surik.simyan.aliasika.GameViewModel
import surik.simyan.aliasika.android.presentation.screens.*
import surik.simyan.aliasika.android.presentation.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<GameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController) }
                    composable("gamemode") { GamemodeScreen(navController, viewModel) }
                    composable("points") { PointsScreen(navController, viewModel) }
                    composable("time") { TimeScreen(navController, viewModel) }
                    composable("teams") { TeamsScreen(navController, viewModel) }
                    composable("score") { GameScoreScreen(navController, viewModel) }
                    composable("standard") { GameStandardScreen(navController, viewModel) }
                    composable("swipe") { GameSwipeScreen(navController, viewModel) }
                    composable("stack") { GameStackScreen(navController, viewModel) }
                    composable("winner") { GameWinnerScreen(navController, viewModel) }
                }
            }
        }
    }
}
