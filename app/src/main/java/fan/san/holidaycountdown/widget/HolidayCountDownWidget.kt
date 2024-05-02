package fan.san.holidaycountdown.widget

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import fan.san.holidaycountdown.common.WidgetStyles

/**
 *@author  范三
 *@version 2024/5/2
 */

class HolidayCountDownWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
	        val prefs = currentState<Preferences>()
            GlanceTheme {
				Log.d("fansangg", "widget update -- ${prefs[WidgetStyles.backgroundAlpha]}")
                Box(modifier = GlanceModifier.fillMaxSize().background(Color.Red.copy(alpha = prefs[WidgetStyles.backgroundAlpha]?:1f)), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Hello Glance", style = TextDefaults.defaultTextStyle.copy(
                            fontSize = 16.sp, color = ColorProvider(
                                Color.Blue
                            )
                        )
                    )
                }
            }
        }
    }
}