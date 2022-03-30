package com.example.taskscheduler.data.sources.local

import android.content.Context
import androidx.room.Room
import com.example.taskscheduler.data.sources.local.dao.ADao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {
    private const val DATABASE_NAME = "local_database"

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context, LocalDataBase::class.java, DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun providesADao(db: LocalDataBase) = db.aDao
}
