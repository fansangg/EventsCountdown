package fan.san.eventscountdown.widget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.hilt.android.EntryPointAccessors
import fan.san.eventscountdown.common.todayZeroTime
import fan.san.eventscountdown.db.Logs
import fan.san.eventscountdown.db.WidgetWithEvents
import fan.san.eventscountdown.repository.CountdownRepository
import fan.san.eventscountdown.repository.EventsCountdownEntryPoint
import fan.san.eventscountdown.utils.CommonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
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
                        24.hours.toJavaDuration()
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

        val entryPoint = EntryPointAccessors.fromApplication<EventsCountdownEntryPoint>(context)
        val repository = entryPoint.getCountdownRepository()
        val widgetInfoRepository = entryPoint.getWidgetInfoRepository()
        repository.insertLogs(Logs.create("workmanager -- doWork"))
        repository.deleteBefore7()

        GlanceAppWidgetManager(context).apply {
            val glanceIds = getGlanceIds(EventsCountdownWidget::class.java)
            glanceIds.forEach { glanceId ->
                val ret = withContext(Dispatchers.IO) {
                    widgetInfoRepository.queryWidgetWithEvents(getAppWidgetId(glanceId))
                }
                if (ret.isNotEmpty()) {
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[CountdownWidgetStateKeys.title] =
                            ret.first().events.first { it.startDateTime > System.currentTimeMillis() }.title
                        prefs[CountdownWidgetStateKeys.date] =
                            ret.first().events.first { it.startDateTime > System.currentTimeMillis() }.startDateTime
                    }

                    logEventInfo(repository, ret)
                }
            }
            val deleteResult =
                widgetInfoRepository.deleteIfNotExists(glanceIds.map { getAppWidgetId(it) })
            Log.d("fansangg", "doWork: deleteResult == $deleteResult")
            EventsCountdownWidget().updateAll(context)
        }

        Log.d("fansangg", "UpdateWidgetWorker#doWork")
        return Result.success()
    }

    private suspend fun logEventInfo(
        repository: CountdownRepository,
        ret: List<WidgetWithEvents>
    ) {
        withContext(Dispatchers.IO) {
            repository.insertLogs(
                Logs.create(
                    """
                    --- 小组件更新 ---
                    ${
                        if (ret.first().events.isEmpty()) "无下一个事件" else "下一个事件=${ret.first().events.first().title},剩余=${
                            CommonUtil.getDaysDiff(
                                ret.first().events.first().startDateTime
                            )
                        }"
                    }
                """.trimIndent()
                )
            )
        }
    }

}