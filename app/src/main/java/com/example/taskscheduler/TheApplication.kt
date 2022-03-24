package com.example.taskscheduler

import android.app.Application
import android.widget.Toast
import timber.log.Timber

class TheApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        //if (BuildConfig.DEBUG)
        Timber.plant(Timber.DebugTree())

        Toast.makeText(
            this,
            "Aplicación iniciada",
            Toast.LENGTH_LONG
        ).show()
    }
}
