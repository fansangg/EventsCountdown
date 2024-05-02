package fan.san.holidaycountdown.page

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import fan.san.holidaycountdown.common.ColumnWithTitle
import fan.san.holidaycountdown.common.WidgetStyles
import fan.san.holidaycountdown.common.dataStore
import fan.san.holidaycountdown.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun WidgetSettingsPage(glanceId:Int){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentAlpha by remember {
        mutableFloatStateOf(1f)
    }

    LaunchedEffect(key1 = Unit) {
        context.dataStore.data.map { it[WidgetStyles.backgroundAlpha] ?: 1f }
            .collect{
                currentAlpha = it
            }
    }

    val viewModel = hiltViewModel<SettingsViewModel>()
    ColumnWithTitle(title = "小组件设置", showAction = true, showBack = false,actionIcon = Icons.Default.Done,actionClick = {
        (context as Activity).setResult(Activity.RESULT_OK)
        scope.launch {
            viewModel.updateWidgetAlpha(currentAlpha)
            context.finish()
        }
    }) {

        Column(modifier = Modifier
            .fillMaxSize()) {

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Text(text = "${(currentAlpha * 100).roundToInt()}%")
                Slider(value = currentAlpha, onValueChange = {
                    currentAlpha = it
                }, steps = 9, valueRange = 0f..1f, modifier = Modifier.weight(1f))
            }
        }
    }

}