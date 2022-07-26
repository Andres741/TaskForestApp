package com.example.taskscheduler.util.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskSchedulerApp


class BroadcastNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Build notification based on Intent

        val title = intent.getStringExtra("title") ?: return
        val text = intent.getStringExtra("text") ?: return
//        val text = (intent.getStringExtra("text") ?: "") + " Extra: ${(0..128).random()}"

        val notification = NotificationCompat.Builder(context, channelId).run {
            setSmallIcon(R.mipmap.ic_launcher_round)
            setContentTitle(title)
            setContentText(text.let {
                it// + " Extra: ${(0..128).random()}"
            })
            priority = NotificationCompat.PRIORITY_DEFAULT

            build()
        }

        NotificationManagerCompat.from(context).run {
//            cancel(ID)
            notify(ID, notification)
        }
        cancelNotification(context, title, text)
    }

    companion object {
        private const val ID = 58
        private val channelId by lazy { TaskSchedulerApp.INSTANCE.getString(R.string.CHANNEL_ID) }

        fun scheduleNotification(context: Context, time: Long, title: String, text: String) {
            val intent = Intent(context, BroadcastNotificationReceiver::class.java)
            intent.putExtra("title", title)
            intent.putExtra("text", text)
            val pending =
                PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_IMMUTABLE)
            // Schdedule notification
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending)
//            manager.set(AlarmManager.RTC_WAKEUP, time, pending)
        }

        fun cancelNotification(context: Context, title: String, text: String) {
            val intent = Intent(context, BroadcastNotificationReceiver::class.java)
            intent.putExtra("title", title)
            intent.putExtra("text", text)
            val pending =
                PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_IMMUTABLE)
            // Cancel notification
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(pending)
        }
    }
}

// Example

//NotificationReceiver.cancelNotification(
//    context, title, text
//)
//
//BroadcastNotificationReceiver.scheduleNotification(
//    context, notifyTime, title, text
//)
