package com.example.taskscheduler

import android.app.Application
//import androidx.work.ExistingPeriodicWorkPolicy
//import androidx.work.WorkManager
//import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskSchedulerApp: Application() {
    override fun onCreate() {
        super.onCreate()
        _INSTANCE = this
    }

    companion object {
        private lateinit var _INSTANCE: TaskSchedulerApp
        val INSTANCE: TaskSchedulerApp
            get() = _INSTANCE
    }
}
