package surik.simyan.aliasika.android.presentation.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.koin.androidx.compose.get
import surik.simyan.aliasika.presentation.MainViewModel

@Composable
fun GameWinnerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = get<MainViewModel>()
    Surface(color = MaterialTheme.colors.secondary) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                viewModel.gameWinnerText().toString(context),
                fontSize = 72.sp,
                textAlign = TextAlign.Center
            )
        }
    }
    Handler(Looper.getMainLooper()).postDelayed({
        viewModel.resetValues()
        navController.popBackStack()
    }, 5000)
}
