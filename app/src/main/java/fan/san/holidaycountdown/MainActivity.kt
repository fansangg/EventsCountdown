package fan.san.holidaycountdown

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import fan.san.holidaycountdown.common.WidgetStyles
import fan.san.holidaycountdown.common.dataStore
import fan.san.holidaycountdown.ui.theme.HolidayCountDownTheme
import fan.san.holidaycountdown.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			HolidayCountDownTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					WidgetSettings(modifier = Modifier.padding(innerPadding))
				}
			}
		}
	}
}

@Composable
private fun WidgetSettings(modifier: Modifier) {

	val context = LocalContext.current
	val alpha = context.dataStore.data.map { it[WidgetStyles.backgroundAlpha] ?: 0f }
		.collectAsState(initial = 0f)
	val viewModel = viewModel<SettingsViewModel>()
	Column(modifier = modifier
		.fillMaxSize()
		.padding(horizontal = 30.dp)) {
		Slider(value = alpha.value, onValueChange = {
			viewModel.updateWidgetAlpha(it)
		}, steps = 9, valueRange = 0f..1f)

		Text(text = "currentValue = ${(alpha.value * 100).roundToInt()}%")
	}
}


