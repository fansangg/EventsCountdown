package fan.san.eventscountdown.widget

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

/**
 *@author  范三
 *@version 2024/5/2
 */

class EventsCountdownWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val defaultTextStyle = TextStyle(color = ColorProvider(Color.White))
            GlanceTheme {
                Column(
                    modifier = GlanceModifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        .fillMaxSize().background(Color.Black.copy(alpha =  prefs[WidgetStyles.backgroundAlpha]?:1f))
                ) {
                    Text(
                        text = "劳动节",
                        style = defaultTextStyle.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )

                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Row {
                            Text(text = "剩余 ", style = defaultTextStyle)
                            Text(
                                text = "365",
                                style = defaultTextStyle.copy(
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(text = " 天", style = defaultTextStyle)
                        }
                    }
                }
            }
        }
    }

    override fun onCompositionError(
        context: Context,
        glanceId: GlanceId,
        appWidgetId: Int,
        throwable: Throwable
    ) {
        super.onCompositionError(context, glanceId, appWidgetId, throwable)
        Log.d("fansangg", "onCompositionError: ${throwable.message}")
    }
}