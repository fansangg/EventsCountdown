package fan.san.eventscountdown.repository

import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.android.qualifiers.ApplicationContext
import fan.san.eventscountdown.db.EventWidgetCrossRef
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.db.WidgetInfo
import fan.san.eventscountdown.widget.CountdownWidgetStateKeys
import fan.san.eventscountdown.widget.EventsCountdownWidget
import fan.san.eventscountdown.widget.UpdateCountdownWidgetWorker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetsInfoRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: WidgetInfo.WidgetInfoDao,
    private val refDao:EventWidgetCrossRef.EventWidgetCrossRefDao
){
    fun queryById(id:Int) = dao.queryById(id)

    fun deleteById(id:Int) = dao.delete(id)

    fun insert(widgetInfo: WidgetInfo) = dao.insert(widgetInfo)

    fun deleteIfNotExists(ids:List<Int>) = dao.deleteIfNotExists(ids)

    fun insertEventWidgetCrossRef(eventWidgetCrossRefList: List<EventWidgetCrossRef>) = refDao.insert(eventWidgetCrossRefList)

    fun queryWidgetWithEvents(id:Int) = dao.getWidgetEvents(id)

    suspend fun updateWidgetInfoState(widgetInfo: WidgetInfo, nextEvents:Events){
        GlanceAppWidgetManager(context).apply {
            updateAppWidgetState(context, getGlanceIdBy(widgetInfo.id)) {
                it[CountdownWidgetStateKeys.backgroundColor] = widgetInfo.color.toArgb()
                it[CountdownWidgetStateKeys.title] = nextEvents.title
                it[CountdownWidgetStateKeys.date] = nextEvents.startDateTime
                it[CountdownWidgetStateKeys.isFollowSystem] = widgetInfo.followSystem
            }
            UpdateCountdownWidgetWorker.enqueuePeriodWork(context,getGlanceIdBy(widgetInfo.id))
            EventsCountdownWidget().update(context, getGlanceIdBy(widgetInfo.id))
        }
    }


}