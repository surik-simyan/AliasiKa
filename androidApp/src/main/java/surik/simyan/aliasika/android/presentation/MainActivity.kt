package surik.simyan.aliasika.android.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import surik.simyan.aliasika.android.presentation.screens.*
import surik.simyan.aliasika.android.presentation.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController) }
                    composable("gamemode") { GamemodeScreen(navController) }
                    composable("points") { PointsScreen(navController) }
                    composable("time") { TimeScreen(navController) }
                    composable("teams") { TeamsScreen(navController) }
                    composable("score") { GameScoreScreen(navController) }
                    composable("standard") { GameStandardScreen(navController) }
                    composable("swipe") { GameSwipeScreen(navController) }
                    composable("stack") { GameStackScreen(navController) }
                    composable("winner") { GameWinnerScreen(navController) }
                }
            }
        }
    }
}
