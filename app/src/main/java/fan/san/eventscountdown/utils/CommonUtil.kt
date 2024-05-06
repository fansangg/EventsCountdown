package fan.san.eventscountdown.utils

import android.content.Context
import android.icu.util.Calendar
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.WorkManager
import fan.san.eventscountdown.common.dataStore
import fan.san.eventscountdown.entity.TestLogBean
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
        val todayTime = System.currentTimeMillis().milliseconds
        val diff = todayTime - targetTime.milliseconds
        return diff.absoluteValue.inWholeDays.toString()
    }

    fun startEventsCountdownWork(context: Context){
        val workManager = WorkManager.getInstance(context)
//        OneTimeWorkRequestBuilder<UpdateWidgetWorker>()
//            .setInitialDelay(1.milliseconds)
    }

    suspend fun saveLog(context: Context, testLogBean: TestLogBean){
       val logs = stringPreferencesKey("logs")
        /*  val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
         val parameterizedType  = Types.newParameterizedType(List::class.java,TestLogBean::class.java)
         val adapter = moshi.adapter<List<TestLogBean>>(parameterizedType)*/
        var savedData = context.dataStore.data.map {
            it[logs]?:""
        }.first()
        /*val savedList = adapter.fromJson(savedData)?.toMutableList()?: mutableListOf()
        savedList.add(testLogBean)
        val newData = adapter.toJson(savedList)*/
        savedData += testLogBean.toString()
        context.dataStore.edit {
            it[logs] = savedData
        }
    }
}