package fan.san.eventscountdown

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import fan.san.eventscountdown.navigation.Routes
import fan.san.eventscountdown.page.NavHostPage
import fan.san.eventscountdown.ui.theme.EventsCountdownTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appWidgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val startDestination =
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) Routes.Setting(appWidgetId) else Routes.Home
        setContent {
            EventsCountdownTheme {
                NavHostPage(startDestination)
            }
        }
    }
}


