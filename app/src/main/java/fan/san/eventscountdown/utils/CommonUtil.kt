package fan.san.eventscountdown.utils

import android.icu.util.Calendar
import fan.san.eventscountdown.common.todayZeroTime
import kotlin.time.Duration.Companion.milliseconds

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
}