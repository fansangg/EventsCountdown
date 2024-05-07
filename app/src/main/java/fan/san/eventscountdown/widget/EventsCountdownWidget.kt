package fan.san.eventscountdown.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import dagger.hilt.android.EntryPointAccessors
import fan.san.eventscountdown.db.Logs
import fan.san.eventscountdown.utils.CommonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 *@author  范三
 *@version 2024/5/2
 */
class EventsCountdownWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val repository =
            EntryPointAccessors.fromApplication<WidgetEntryPoint>(context).getCountdownRepository()
        val nextEvents = withContext(Dispatchers.IO) {
            repository.insertLogs(Logs.create("provideGlance#1"))
            repository.getNextEvents()
        }
        Log.d("fansangg", "EventsCountdownWidget#provideGlance:#1")
        provideContent {
            val prefs = currentState<Preferences>()
            val backgroundColorArgb =
                prefs[CountdownWidgetStyles.backgroundColor] ?: Color.Black.toArgb()
            val backgroundColor = Color(backgroundColorArgb)
            val defaultTextStyle =
                TextStyle(color = ColorProvider(if (isLightColor(backgroundColor)) Color.Black else Color.White))
            LaunchedEffect(key1 = null) {
                withContext(Dispatchers.IO) {
                    repository.insertLogs(Logs.create("provideGlance#2"))
                }
            }
            SideEffect {
                UpdateWidgetWorker.enqueuePeriodWork(context, id)
                Log.d("fansangg", "EventsCountdownWidget#provideGlance: enqueueUniqueWork")
            }
            GlanceTheme {
                Box(
                    modifier = GlanceModifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        .fillMaxSize().background(
                            backgroundColor
                        ), contentAlignment = Alignment.Center
                ) {

                    if (nextEvents.isEmpty()) {
                        Text(
                            text = "当前无事件", style = defaultTextStyle.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        )
                    } else
                        Column(modifier = GlanceModifier.fillMaxSize()) {
                            Text(
                                text = nextEvents.first().title,
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
                                        text = CommonUtil.getDaysDiff(nextEvents.first().startDateTime),
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
    }

    private fun isLightColor(color: Color): Boolean {
        val luminance = (0.2126 * color.red + 0.7152 * color.green + 0.0722 * color.blue)
        return luminance > 0.5
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

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        Log.d("fansangg", "EventsCountdownWidget#onDelete: ")
        UpdateWidgetWorker.cancel(context, glanceId)
    }
}