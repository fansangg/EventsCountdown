package fan.san.eventscountdown.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint
import fan.san.eventscountdown.db.Logs
import fan.san.eventscountdown.repository.CountdownRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *@author  范三
 *@version 2024/5/2
 */

@AndroidEntryPoint
class EventsCountdownWidgetReceiver :
    GlanceAppWidgetReceiver() {

    @Inject
    lateinit var repository: CountdownRepository

    override val glanceAppWidget: GlanceAppWidget = EventsCountdownWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            repository.insertLogs(Logs.create("onReceive"))
            Log.d("fansangg", "EventsCountdownWidgetReceiver#onReceive:")
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
            repository.insertLogs(Logs.create("onUpdate"))
            Log.d("fansangg", "EventsCountdownWidgetReceiver#onUpdate:")
        }
    }
}