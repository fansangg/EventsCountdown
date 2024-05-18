package fan.san.eventscountdown.repository

import android.content.ContentUris
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import fan.san.eventscountdown.common.todayZeroTime
import fan.san.eventscountdown.db.Events
import fan.san.eventscountdown.db.Logs
import fan.san.eventscountdown.entity.CalendarAccountBean
import fan.san.eventscountdown.utils.CommonUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CountdownRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val eventsDao: Events.EventsDao,
    private val logsDao: Logs.LogsDao
) {

    fun getAllEvents(): Flow<List<Events>> = eventsDao.query()
    suspend fun insertEvents(events: Events) = eventsDao.insert(events)
    suspend fun insertEvents(events: List<Events>) = eventsDao.insert(events)
    suspend fun delete(events: Events) = eventsDao.delete(events)
    suspend fun update(isShow: Boolean, id: Long) = eventsDao.update(if (isShow) 1 else 0, id)
    fun getNextEvents() = eventsDao.getNextEvents(System.currentTimeMillis().todayZeroTime)

    fun getAllTags() = eventsDao.getTags()

    fun deleteBefore7() = logsDao.deleteBefore7()

    fun insertLogs(log:Logs) = logsDao.insert(log)

    fun queryLogsByDate(date:String):Flow<List<Logs>> = logsDao.queryByDate(date)

    fun queryLogDates() : List<String> = logsDao.queryDates()

    fun queryCalendarAccounts(): List<CalendarAccountBean> {
        val list = mutableListOf<CalendarAccountBean>()
        context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI, arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.CALENDAR_COLOR_KEY,
            ), null, null, null
        )?.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Calendars._ID))
                    val name =
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME))
                    val displayName =
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                    val type =
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_TYPE))
                    val color =
                        it.getInt(it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_COLOR))
                    list.add(CalendarAccountBean(id, name, displayName, type, color))
                } while (it.moveToNext())
            }
        }

        Log.d(
            "fansangg",
            "CountdownRepository#queryCalendarAccounts: Accounts == ${list.joinToString()}"
        )
        return list
    }

    fun getCalendarEvents(calendarId: Long): List<Events> {
        val list = mutableListOf<Events>()
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, CommonUtil.getThisYearFristTime())
        ContentUris.appendId(builder, CommonUtil.getThisYearLastTime())
        context.contentResolver.query(
            builder.build(),
            arrayOf(
                CalendarContract.Instances._ID,
                CalendarContract.Instances.START_DAY,
                CalendarContract.Instances.END_DAY,
                CalendarContract.Instances.START_MINUTE,
                CalendarContract.Instances.END_MINUTE,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.DESCRIPTION,
                CalendarContract.Instances.ALL_DAY,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.CALENDAR_DISPLAY_NAME,
                CalendarContract.Instances.END
            ),
            "(${CalendarContract.Instances.CALENDAR_ID} = ?)",
            arrayOf("$calendarId"),
            "${CalendarContract.Instances.START_DAY} asc"
        )?.use {
            if (it.moveToFirst()) {
                do {
                    val title =
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Instances.TITLE))
                    val id = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances._ID))
                    val description =
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION))
                    val isAllDay =
                        it.getInt(it.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY))
                    val begin =
                        it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN))
                    val name =
                        it.getString(it.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_DISPLAY_NAME))
                    val end = it.getLong(it.getColumnIndexOrThrow(CalendarContract.Instances.END))
                    list.add(Events(id, title, begin.todayZeroTime, 1, tag = name))
                } while (it.moveToNext())
            }
        }

        return list
    }
}