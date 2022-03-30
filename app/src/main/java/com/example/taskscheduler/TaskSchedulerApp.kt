package com.example.taskscheduler

import android.app.Application
//import androidx.work.ExistingPeriodicWorkPolicy
//import androidx.work.WorkManager
//import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TaskSchedulerApp: Application() {
    override fun onCreate() {
        super.onCreate()

        //if (BuildConfig.DEBUG)
        Timber.plant(Timber.DebugTree())

//        Toast.makeText(
//            this,
//            "Aplicación iniciada",
//            Toast.LENGTH_LONG
//        ).show()
    }
}
