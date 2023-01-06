package surik.simyan.aliasika.android.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import surik.simyan.aliasika.SharedStrings

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    Surface(color = MaterialTheme.colors.secondary) {
        Box(
            modifier = Modifier.fillMaxSize(1.0f)
                .background(color = MaterialTheme.colors.secondary)
                .padding(8.dp)
        ) {
            Card(
                modifier = Modifier.align(Alignment.Center).clickable {
                    navController.navigate("gamemode")
                },
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(0.dp, 64.dp, 0.dp, 64.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(SharedStrings.play.toString(context), fontWeight = FontWeight.Normal, fontSize = 48.sp)
                }
            }
        }
    }
}
