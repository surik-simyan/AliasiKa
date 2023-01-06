package surik.simyan.aliasika.android.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.FWheelPickerFocusVertical
import surik.simyan.aliasika.GameViewModel
import surik.simyan.aliasika.SharedStrings

@Composable
fun GamemodeScreen(navController: NavHostController, viewModel: GameViewModel) {
    val context = LocalContext.current
    val gamemodeRange = mutableListOf(
        SharedStrings.gamemodeStandard.toString(context),
        SharedStrings.gamemodeSwipe.toString(context),
        SharedStrings.gamemodeStack.toString(context)
    )
    var pickerValue by remember { mutableStateOf(gamemodeRange[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.VideogameAsset,
            "",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = SharedStrings.playGamemode.toString(context),
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))
        FVerticalWheelPicker(
            focus = {
                FWheelPickerFocusVertical(
                    dividerColor = MaterialTheme.colors.primary,
                    dividerSize = 2.dp
                )
            },
            count = gamemodeRange.size,
            onIndexChanged = {
                pickerValue = gamemodeRange[it]
            }
        ) { index ->
            Text(gamemodeRange[index], color = MaterialTheme.colors.onBackground)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.gamemode = when (pickerValue) {
                    gamemodeRange[0] -> GameViewModel.Gamemode.STANDARD
                    gamemodeRange[1] -> GameViewModel.Gamemode.SWIPE
                    gamemodeRange[2] -> GameViewModel.Gamemode.STACK
                    else -> GameViewModel.Gamemode.STANDARD
                }
                navController.navigate("points")
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                SharedStrings.next.toString(context),
                color = Color.White,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
