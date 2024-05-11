package fan.san.eventscountdown.widget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.hilt.android.EntryPointAccessors
import fan.san.eventscountdown.common.todayZeroTime
import fan.san.eventscountdown.db.Logs
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

class UpdateWidgetWorker(
    private val context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        fun enqueuePeriodWork(context: Context, id: GlanceId) {
            val tomorrow =
                (System.currentTimeMillis().milliseconds + 1.days).inWholeMilliseconds.todayZeroTime
            val delay = tomorrow - System.currentTimeMillis()
            Log.d("fansangg", "UpdateWidgetWorker#enqueuePeriodWork: tomorrow == $tomorrow")
            Log.d("fansangg", "UpdateWidgetWorker#enqueuePeriodWork: delay == ${delay.milliseconds.inWholeMinutes}")
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "${EventsCountdownWidget::class.java.simpleName}-periodic-worker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    PeriodicWorkRequest.Builder(
                        UpdateWidgetWorker::class.java,
                        24.hours.toJavaDuration()
                    ).addTag(id.toString()).setInitialDelay(delay, timeUnit = TimeUnit.MILLISECONDS)
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

        fun cancel(context: Context, id: GlanceId) {
            val repository = EntryPointAccessors.fromApplication<WidgetEntryPoint>(context)
                .getCountdownRepository()
            repository.insertLogs(Logs.create("小组件移除 workmanager -- cancel id == $id"))
            Log.d("fansangg", "UpdateWidgetWorker#cancel: 小组件移除 cancel id == $id")
            WorkManager.getInstance(context).cancelAllWorkByTag(id.toString())
        }
    }

    override suspend fun doWork(): Result {
        val repository =
            EntryPointAccessors.fromApplication<WidgetEntryPoint>(context).getCountdownRepository()
        repository.insertLogs(Logs.create("workmanager -- doWork"))
        repository.deleteBefore7()
        Log.d("fansangg", "UpdateWidgetWorker#doWork")
        EventsCountdownWidget().apply {
            updateAll(context)
        }

        return Result.success()
    }

}