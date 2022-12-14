package com.example.taskscheduler.util.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.taskscheduler.R
import java.util.concurrent.TimeUnit

class NotificationWorker(
    val context: Context, workerParams: WorkerParameters,
): Worker(context, workerParams) {

    override fun doWork(): Result {

        val title = inputData.getString(TITLE) ?: return Result.failure()
        val text = inputData.getString(TEXT) ?: return Result.failure()
        val channelId = inputData.getString(CHANNEL_ID) ?: return Result.failure()
        val notificationId = inputData.getString(NOTIFICATION_ID)?.toIntOrNull() ?: return Result.failure()

        val notification = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.mipmap.ic_launcher_round)
            setContentTitle(title)
            setContentText(text)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }.build()

        NotificationManagerCompat.from(context).run {
            notify(notificationId, notification)
        }

        return Result.success()
    }

    companion object {
        const val TITLE = "a"
        const val TEXT = "b"
        const val CHANNEL_ID = "c"
        const val NOTIFICATION_ID = "d"

        fun crateData(
            title: String, text: String, channelId: String, notificationId: Int
        ) = Data.Builder().apply {
            putString(TITLE, title)
            putString(TEXT, text)
            putString(CHANNEL_ID, channelId)
            putString(NOTIFICATION_ID, notificationId.toString())
        }.build()

        fun sendOneNotification (
            title: String, text: String, channelId: String, notificationId: Int,
            context: Context, workTag: String? = null, delayMillis: Long? = null,
        ) {
            val data = crateData(title, text, channelId, notificationId)

            val notificationWorkRequest: OneTimeWorkRequest =
                OneTimeWorkRequestBuilder<NotificationWorker>().apply {
                    setInputData(data)

                    delayMillis?.takeIf { it > 0 }?.also {
                        setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                    }
                    workTag?.also { addTag(workTag) }
                }.build()

            WorkManager
                .getInstance(context)
                .enqueue(notificationWorkRequest)
        }
    }
}

interface NotificationFactory {
    val context: Context
    val channelId: String

    fun sendNotification(
        title: String, text: String, notificationId: Int,
        delayMillis: Long?, workTag: String?
    ) {
        NotificationWorker.sendOneNotification(
            title, text, channelId, notificationId, context, workTag, delayMillis
        )
    }
    fun cancelNotificationByTag(workTag: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(workTag)
    }
    fun deleteTaskById(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}

// Example

//val data = NotificationWorker.crateData("title", "text", "channelId", 77)
//
//val notificationWorkRequest =
//    OneTimeWorkRequestBuilder<NotificationWorker>()
//        .setInitialDelay(15_000L, TimeUnit.MILLISECONDS)
//        .setInputData(data)
//        .addTag(WORK_TAG)
//        .build()
//
//WorkManager
//.getInstance(context)
//.enqueue(notificationWorkRequest)
