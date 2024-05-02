package fan.san.holidaycountdown.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 *@author  范三
 *@version 2024/5/2
 */

class HolidayCountDownWidgetReceiver :GlanceAppWidgetReceiver(){

    override val glanceAppWidget: GlanceAppWidget = HolidayCountDownWidget()
}