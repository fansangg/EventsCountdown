package fan.san.eventscountdown.utils

import android.content.Context
import android.icu.util.Calendar
import android.icu.util.LocaleData
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.WorkManager
import fan.san.eventscountdown.common.dataStore
import fan.san.eventscountdown.common.todayZeroTime
import fan.san.eventscountdown.entity.TestLogBean
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

object CommonUtil {

    fun getThisYearFristTime():Long{
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        calendar.set(year,0,1)
        return calendar.timeInMillis
    }


    fun getThisYearLastTime():Long{
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        calendar.set(year,11,31)
        return calendar.timeInMillis
    }

    fun getDaysDiff(targetTime:Long):String{
        val todayTime = System.currentTimeMillis().todayZeroTime.milliseconds
        val diff = todayTime - targetTime.milliseconds
        return diff.absoluteValue.inWholeDays.toString()
    }

    fun startEventsCountdownWork(context: Context){
        val workManager = WorkManager.getInstance(context)
//        OneTimeWorkRequestBuilder<UpdateWidgetWorker>()
//            .setInitialDelay(1.milliseconds)
    }
}