package fan.san.eventscountdown.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class UpdateWidgetWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {


    companion object{
        fun enqueue(context: Context,id:GlanceId,days:String,title:String){
            val workManager = WorkManager.getInstance(context)
            val requestBuilder = OneTimeWorkRequestBuilder<UpdateWidgetWorker>().apply {
                addTag(id.toString())
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    workDataOf(
                        "days" to days,
                        "title" to title
                    )
                )
            }
            workManager.enqueueUniqueWork("UpdateWidgetWorker$days",
                ExistingWorkPolicy.REPLACE,requestBuilder.build())
        }
    }

    override suspend fun doWork(): Result {
        val days = workerParams.inputData.getString("days")?:""
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
        }
    }

}