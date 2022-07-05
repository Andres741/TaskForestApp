package com.example.taskscheduler.di.data

import android.content.Context
import androidx.room.Room
import com.example.taskscheduler.data.ALocalRepository
import com.example.taskscheduler.data.Converters
import com.example.taskscheduler.data.sources.local.LocalDataBase
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
        ).addTypeConverter(Converters()).build()

    @Singleton
    @Provides
    fun providesADao(db: LocalDataBase) = db.aDao

    @Singleton
    @Provides
    fun providesTaskDao(db: LocalDataBase) = db.taskDao

    @Singleton
    @Provides
    fun providesSubTaskDao(db: LocalDataBase) = db.subTaskDao
}
