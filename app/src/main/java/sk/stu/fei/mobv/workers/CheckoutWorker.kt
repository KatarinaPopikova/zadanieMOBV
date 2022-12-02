package sk.stu.fei.mobv.workers

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import sk.stu.fei.mobv.R
import sk.stu.fei.mobv.network.BarMessageBody
import sk.stu.fei.mobv.network.RestApiService

class CheckoutWorker(val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            1, createNotification()
        )
    }

    override suspend fun doWork(): Result {
        val response =
            RestApiService.create(appContext).barMessage(BarMessageBody("", "", "", 0.0, 0.0))
        return if (response.isSuccessful) Result.success() else Result.failure()
    }

    private fun createNotification(): Notification {
        val builder =
            NotificationCompat.Builder(appContext, "mobv2022").apply {
                setContentTitle("MOBV 2022")
                setContentText("Exiting bar ...")
                setSmallIcon(R.drawable.ic_location)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }

        return builder.build()
    }


}