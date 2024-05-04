package fan.san.eventscountdown.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint
import fan.san.eventscountdown.repository.CountdownRepository
import javax.inject.Inject

/**
 *@author  范三
 *@version 2024/5/2
 */

@AndroidEntryPoint
class EventsCountdownWidgetReceiver :GlanceAppWidgetReceiver(){

    @Inject lateinit var repository: CountdownRepository

    override val glanceAppWidget: GlanceAppWidget = EventsCountdownWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

    }
}