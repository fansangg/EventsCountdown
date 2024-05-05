package fan.san.eventscountdown.common

import LunarDateUtil
import android.content.Context
import android.icu.util.Calendar
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 *@author  范三
 *@version 2024/5/2
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widgetInfos")

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

inline val Long.todayZeroTime get() = todayZeroTime(this)
inline val Long.formatMd get() = formatMd(this)

inline val Long.getWeekDay get() = getWeekDay(this)

inline val Long.toLunr get() = toLunar(this)

fun toLunar(time: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    val lunar = LunarDateUtil.solar2lunar(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    return "${lunar?.iMonthCn}${lunar?.iDayCn}"
}

fun getWeekDay(time: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    val weekDay = arrayOf("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日")
    return weekDay[calendar.get(Calendar.DAY_OF_WEEK) - 1]
}

fun formatMd(time: Long): String {
    return DateFormat.format("M月d日", time).toString()
}

fun todayZeroTime(time: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}