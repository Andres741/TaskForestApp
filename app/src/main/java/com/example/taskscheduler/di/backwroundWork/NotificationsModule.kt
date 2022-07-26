package com.example.taskscheduler.di.backwroundWork

import android.content.Context
import com.example.taskscheduler.R
import com.example.taskscheduler.util.notifications.NotificationFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationsModule {
    @Singleton
    @Provides
    fun provideAdviseDateNotificationFactory(@ApplicationContext context: Context) =
        object : AdviseDateNotificationFactory {
            override val context: Context = context
            override val channelId: String = context.getString(R.string.CHANNEL_ID)
        }
}

interface AdviseDateNotificationFactory: NotificationFactory
