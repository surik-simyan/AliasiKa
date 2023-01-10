package surik.simyan.aliasika.android.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.FWheelPickerFocusVertical
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.get
import surik.simyan.aliasika.presentation.MainViewModel
import surik.simyan.aliasika.SharedStrings

@Composable
fun PointsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = get<MainViewModel>()
    val pointsRange = (5..155).step(5).toList()
    var pickerValue by remember { mutableStateOf(pointsRange[19]) }
    val state = rememberFWheelPickerState(19)
    LaunchedEffect(state) {
        delay(2000)
        // Scroll to index.
        state.animateScrollToIndex(19)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            "",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = SharedStrings.playPoints.toString(context = LocalContext.current),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colors.onBackground
        )
        FVerticalWheelPicker(
            focus = {
                FWheelPickerFocusVertical(
                    dividerColor = MaterialTheme.colors.primary,
                    dividerSize = 2.dp
                )
            },
            count = pointsRange.size,
            state = state,
            onIndexChanged = {
                pickerValue = pointsRange[it]
            }
        ) { index ->
            Text(pointsRange[index].toString(), color = MaterialTheme.colors.onBackground)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.points = pickerValue.toFloat()
                navController.navigate("time")
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
