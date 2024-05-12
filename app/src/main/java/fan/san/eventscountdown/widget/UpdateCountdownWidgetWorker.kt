package fan.san.eventscountdown.widget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.hilt.android.EntryPointAccessors
import fan.san.eventscountdown.common.todayZeroTime
import fan.san.eventscountdown.db.Logs
import fan.san.eventscountdown.repository.EventsCountdownEntryPoint
import fan.san.eventscountdown.utils.CommonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toJavaDuration

class UpdateCountdownWidgetWorker(
    private val context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        fun enqueuePeriodWork(context: Context, id: GlanceId) {
            val tomorrow =
                (System.currentTimeMillis().milliseconds + 1.days).inWholeMilliseconds.todayZeroTime
            val delay = tomorrow - System.currentTimeMillis()
            Log.d("fansangg", "UpdateWidgetWorker#enqueuePeriodWork: tomorrow == $tomorrow")
            Log.d(
                "fansangg",
                "UpdateWidgetWorker#enqueuePeriodWork: delay == ${
                    delay.milliseconds.toDouble(
                        DurationUnit.MINUTES
                    )
                }"
            )
            val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "${UpdateCountdownWidgetWorker::class.simpleName}",
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    PeriodicWorkRequest.Builder(
                        UpdateCountdownWidgetWorker::class.java,
                        (23.hours + 59.minutes).toJavaDuration()
                    ).setInitialDelay(delay, timeUnit = TimeUnit.MILLISECONDS)
                        .setInputData(inputData = workDataOf("id" to appWidgetId))
                        .build()
                )
        }

        private fun enqueueOneTimeWork(context: Context, id: GlanceId) {
            /*WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "${EventsCountdownWidget::class.java.simpleName}-onetime-worker",
                    ExistingWorkPolicy.KEEP,
                    OneTimeWorkRequest.Builder(UpdateWidgetWorker::class.java)
                        .addTag(id.toString())
                        .setInputData(workDataOf("isOneTime" to true, "delayTime" to diff))
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .build()
                )*/
        }

        fun cancel(context: Context) {
            Log.d(
                "fansangg",
                "UpdateCountdownWidgetWorker#cancel: worker -- ${UpdateCountdownWidgetWorker::class.simpleName} 移除"
            )
            WorkManager.getInstance(context)
                .cancelUniqueWork("${UpdateCountdownWidgetWorker::class.simpleName}")
        }
    }

    override suspend fun doWork(): Result {
        val repository =
            EntryPointAccessors.fromApplication<EventsCountdownEntryPoint>(context)
                .getCountdownRepository()
        repository.insertLogs(Logs.create("workmanager -- doWork"))
        repository.deleteBefore7()
        val nextEvents = withContext(Dispatchers.IO) {
            repository.getNextEvents()
        }

        withContext(Dispatchers.IO) {
            repository.insertLogs(
                Logs.create(
                    """
                --- 小组件更新 ---
                ${
                        if (nextEvents.isEmpty()) "无下一个事件" else "下一个事件=${nextEvents.first().title},剩余=${
                            CommonUtil.getDaysDiff(
                                nextEvents.first().startDateTime
                            )
                        }"
                    }
            """.trimIndent()
                )
            )
        }

        if (nextEvents.isNotEmpty()) {
            GlanceAppWidgetManager(context)
                .apply {
                    getGlanceIdBy(inputData.getInt("id", 0)).let {
                        updateAppWidgetState(context, it) { prefs ->
                            prefs[CountdownWidgetStateKeys.title] = nextEvents.first().title
                            prefs[CountdownWidgetStateKeys.date] = nextEvents.first().startDateTime
                        }

                        EventsCountdownWidget().update(context, it)
                    }
                }
        }

        Log.d("fansangg", "UpdateWidgetWorker#doWork")
        return Result.success()
    }

}