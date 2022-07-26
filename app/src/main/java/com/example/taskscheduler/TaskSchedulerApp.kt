package com.example.taskscheduler

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
//import androidx.work.ExistingPeriodicWorkPolicy
//import androidx.work.WorkManager
//import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskSchedulerApp: Application() {

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val channelId = getString(R.string.CHANNEL_ID)

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        lateinit var INSTANCE: TaskSchedulerApp private set
    }
}
