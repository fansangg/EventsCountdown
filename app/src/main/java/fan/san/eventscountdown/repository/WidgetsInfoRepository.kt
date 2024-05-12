package fan.san.eventscountdown.repository

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.android.qualifiers.ApplicationContext
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.db.WidgetInfos
import fan.san.eventscountdown.widget.CountdownWidgetStateKeys
import fan.san.eventscountdown.widget.EventsCountdownWidget
import fan.san.eventscountdown.widget.UpdateCountdownWidgetWorker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetsInfoRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: WidgetInfos.WidgetInfosDao
){
    fun queryById(id:Int) = dao.queryById(id)

    fun deleteById(id:Int) = dao.delete(id)

    fun insert(widgetInfos: WidgetInfos) = dao.insert(widgetInfos)

    suspend fun updateWidgetInfosState(widgetInfos: WidgetInfos,nextEvents:Events){
        GlanceAppWidgetManager(context).apply {
            updateAppWidgetState(context, getGlanceIdBy(widgetInfos.id)) {
                it[CountdownWidgetStateKeys.backgroundColor] = widgetInfos.color.toArgb()
                it[CountdownWidgetStateKeys.title] = nextEvents.title
                it[CountdownWidgetStateKeys.date] = nextEvents.startDateTime
            }
            UpdateCountdownWidgetWorker.enqueuePeriodWork(context,getGlanceIdBy(widgetInfos.id))
            EventsCountdownWidget().update(context, getGlanceIdBy(widgetInfos.id))
        }
    }
}