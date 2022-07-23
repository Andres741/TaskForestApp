package com.example.taskscheduler.di.util

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Util {

    @Singleton
    @Provides
    fun appDateAndHourFormatProvider() = AppDateAndHourFormatProvider()

    @Singleton
    @Provides
    fun appDateFormatProvider() = AppDateFormatProvider()

}

data class AppDateAndHourFormatProvider(
    val format: SimpleDateFormat = SimpleDateFormat("EEE, dd/MM/yyyy - HH:mm:ss")
)

data class AppDateFormatProvider(
    val format: SimpleDateFormat = SimpleDateFormat("EEE, dd/MM/yyyy")
)
