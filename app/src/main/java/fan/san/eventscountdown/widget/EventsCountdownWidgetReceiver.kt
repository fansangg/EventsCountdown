package fan.san.eventscountdown.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import fan.san.eventscountdown.entity.TestLogBean
import fan.san.eventscountdown.utils.CommonUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *@author  范三
 *@version 2024/5/2
 */

class EventsCountdownWidgetReceiver :GlanceAppWidgetReceiver(){

    override val glanceAppWidget: GlanceAppWidget = EventsCountdownWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            CommonUtil.saveLog(context, TestLogBean(System.currentTimeMillis(),"onReceive"))
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            CommonUtil.saveLog(context, TestLogBean(System.currentTimeMillis(),"onUpdate"))
        }
    }
}