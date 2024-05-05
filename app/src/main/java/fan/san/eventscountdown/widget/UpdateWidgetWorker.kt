package fan.san.eventscountdown.widget

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import fan.san.eventscountdown.common.todayZeroTime
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class UpdateWidgetWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        /*val days = workerParams.inputData.getString("days")?:""
        val title = workerParams.inputData.getString("title")?:""
        if (days.isEmpty() || title.isEmpty())
            return Result.retry()

        GlanceAppWidgetManager(context).apply {
            getGlanceIds(EventsCountdownWidget::class.java).lastOrNull()?.let { id ->
                updateAppWidgetState(context, id) {
                    it[CountdownWidgetInfos.days] = days
                    it[CountdownWidgetInfos.title] = title
                }
                EventsCountdownWidget().update(context, id)
            } ?: run {
                return Result.failure()
            }

            return Result.success()
        }*/

        val tomorrow =
            (System.currentTimeMillis().milliseconds + 1.days).inWholeMilliseconds.todayZeroTime
        Log.d(
            "fansangg",
            "UpdateWidgetWorker#doWork: tomorrow == ${
                DateFormat.format(
                    "yyyy-MM-dd HH:mm:ss",
                    tomorrow
                )
            }"
        )
        val diff = (tomorrow - System.currentTimeMillis()).milliseconds
        if (diff < 1.hours) {
            if (diff > 15.minutes)
                delay(15.minutes)
            else delay(diff)
        } else delay(1.hours)

        Log.d("fansangg", "UpdateWidgetWorker#doWork: doWork")
        EventsCountdownWidget().apply {
            updateAll(context)
        }

        return Result.success()
    }

}