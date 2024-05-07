package fan.san.eventscountdown.widget

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.hilt.android.EntryPointAccessors
import fan.san.eventscountdown.common.todayZeroTime
import fan.san.eventscountdown.db.Logs
import fan.san.eventscountdown.entity.TestLogBean
import fan.san.eventscountdown.utils.CommonUtil
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class UpdateWidgetWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        fun enqueuePeriodWork(context: Context, id: GlanceId) {
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "${EventsCountdownWidget::class.java.simpleName}-periodic-worker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    PeriodicWorkRequest.Builder(
                        UpdateWidgetWorker::class.java,
                        3.hours.toJavaDuration()
                    ).addTag(id.toString()).build()
                )

            enqueueOneTimeWork(context, id)
        }

        private fun enqueueOneTimeWork(context: Context, id: GlanceId) {
            val tomorrow =
                (System.currentTimeMillis().milliseconds + 1.days).inWholeMilliseconds.todayZeroTime
            val diff = tomorrow - System.currentTimeMillis()
            if (diff.minutes <= 30.minutes) {
                WorkManager.getInstance(context)
                    .enqueueUniqueWork(
                        "${EventsCountdownWidget::class.java.simpleName}-onetime-worker",
                        ExistingWorkPolicy.KEEP,
                        OneTimeWorkRequest.Builder(UpdateWidgetWorker::class.java)
                            .addTag(id.toString())
                            .setInputData(workDataOf("isOneTime" to true, "delayTime" to diff))
                            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                            .build()
                    )
            }
        }

        fun cancel(context: Context, id: GlanceId) {
            WorkManager.getInstance(context).cancelAllWorkByTag(id.toString())
        }
    }

    override suspend fun doWork(): Result {
        val isOneTime = inputData.getBoolean("isOneTime", false)
        val repository = EntryPointAccessors.fromApplication<WidgetEntryPoint>(context).getCountdownRepository()
        repository.insertLogs(Logs.create("doWrok -- isOneTime == $isOneTime"))
        Log.d("fansangg", "UpdateWidgetWorker#doWork:isOneTime == $isOneTime")
        if (isOneTime) {
            val delayTime = inputData.getLong("delayTime", 0L)
            delay(delayTime)
        }

        EventsCountdownWidget().apply {
            updateAll(context)
        }

        return Result.success()
    }

}