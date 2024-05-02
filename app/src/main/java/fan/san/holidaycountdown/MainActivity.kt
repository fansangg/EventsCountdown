package fan.san.holidaycountdown

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import dagger.hilt.android.AndroidEntryPoint
import fan.san.holidaycountdown.navigation.Pages
import fan.san.holidaycountdown.page.NavHostPage
import fan.san.holidaycountdown.ui.theme.HolidayCountDownTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		val appWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID)?:AppWidgetManager.INVALID_APPWIDGET_ID
		val startDestination = if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) Pages.Setting.withParam(appWidgetId) else Pages.Home.route
		setContent {
			HolidayCountDownTheme {
				Scaffold {
					NavHostPage(it,startDestination)
				}
			}
		}
	}
}


