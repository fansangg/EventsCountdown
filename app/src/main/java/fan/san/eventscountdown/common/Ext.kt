package fan.san.eventscountdown.common

import LunarDateUtil
import android.icu.util.Calendar
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 *@author  范三
 *@version 2024/5/2
 */

//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widgetInfos")

@Composable
fun Int.SpacerW() {
    Spacer(modifier = Modifier.width(this.dp))
}


@Composable
fun Int.SpacerH() {
    Spacer(modifier = Modifier.height(this.dp))
}

@Composable
fun SpacerW(width: Dp) {
    Spacer(modifier = Modifier.width(width))
}

@Composable
fun SpacerH(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}

inline val Long.todayZeroTime:Long get(){
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}
inline val Long.formatMd:String get(){
    return DateFormat.format("M月d日", this).toString()
}

inline val Long.getWeekDay:String get(){
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val weekDay = arrayOf("星期日","星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
    return weekDay[calendar.get(Calendar.DAY_OF_WEEK) - 1]
}

inline val Long.toLunar:String
    get(){
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        val lunar = LunarDateUtil.solar2lunar(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        return "${lunar?.iMonthCn}${lunar?.iDayCn}"
    }

inline val Color.dynamicTextColor: Color
    get() {
        val luminance =
            (0.2126 * this.red + 0.7152 * this.green + 0.0722 * this.blue) * this.alpha
        return if (luminance > 0.4) Color.Black else Color.White
    }

inline val defaultLightColor:Color
    get() {
        return Color(253, 253, 253)
    }

inline val defaultNightColor:Color
    get() {
        return Color(37, 37, 39)
    }