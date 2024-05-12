package fan.san.eventscountdown.widget

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.android.EntryPointAccessors
import fan.san.eventscountdown.common.dynamicTextColor
import fan.san.eventscountdown.common.formatMd
import fan.san.eventscountdown.common.getWeekDay
import fan.san.eventscountdown.db.Logs
import fan.san.eventscountdown.repository.EventsCountdownEntryPoint
import fan.san.eventscountdown.utils.CommonUtil

/**
 *@author  范三
 *@version 2024/5/2
 */
class EventsCountdownWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        Log.d("fansangg", "EventsCountdownWidget#provideGlance: glanceId == ${GlanceAppWidgetManager(context).getAppWidgetId(id)}")

        provideContent {
            val prefs = currentState<Preferences>()
            val backgroundColorArgb =
                prefs[CountdownWidgetStateKeys.backgroundColor] ?: Color.Black.toArgb()
            val backgroundColor = Color(backgroundColorArgb)
            val defaultTextStyle =
                TextStyle(color = ColorProvider(backgroundColor.dynamicTextColor))

            val title = prefs[CountdownWidgetStateKeys.title]?:""
            val date = prefs[CountdownWidgetStateKeys.date]?:0L

            GlanceTheme {
                Box(
                    modifier = GlanceModifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        .fillMaxSize().background(
                            backgroundColor
                        ), contentAlignment = Alignment.Center
                ) {

                    if (title.isEmpty()) {
                        Text(
                            text = "当前无事件", style = defaultTextStyle.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        )
                    } else
                        Column(modifier = GlanceModifier.fillMaxSize()) {
                            Text(
                                text = title,
                                style = defaultTextStyle.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            )

                            Spacer(modifier = GlanceModifier.height(2.dp))

                            Text(
                                text = "${date.formatMd}  ${date.getWeekDay}",
                                style = defaultTextStyle.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            )

                            Box(
                                modifier = GlanceModifier.fillMaxSize(),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Row {
                                    Text(text = "剩余 ", style = defaultTextStyle.copy(fontSize = 16.sp))
                                    Text(
                                        text = CommonUtil.getDaysDiff(date),
                                        style = defaultTextStyle.copy(
                                            fontSize = 38.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(text = " 天", style = defaultTextStyle.copy(fontSize = 16.sp))
                                }
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

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        Log.d("fansangg", "EventsCountdownWidget#onDelete: ")
        val repository = EntryPointAccessors.fromApplication<EventsCountdownEntryPoint>(context)
            .getCountdownRepository()
        val widgetsInfoRepository =
            EntryPointAccessors.fromApplication<EventsCountdownEntryPoint>(context)
                .getWidgetInfosRepository()
        widgetsInfoRepository.deleteById(GlanceAppWidgetManager(context).getAppWidgetId(glanceId))
        repository.insertLogs(Logs.create("小组件移除 id == $glanceId"))
        Log.d("fansangg", "小组件移除 id == $glanceId")
    }
}