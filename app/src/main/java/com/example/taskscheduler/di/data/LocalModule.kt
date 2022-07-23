package com.example.taskscheduler.di.data

import android.content.Context
import com.example.taskscheduler.data.sources.local.ILocalTaskRepository
import com.example.taskscheduler.data.sources.local.LocalDataBase
import com.example.taskscheduler.data.sources.local.RoomTaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context: Context) = LocalDataBase.build(context)

    @Singleton
    @Provides
    fun providesTaskDao(db: LocalDataBase) = db.taskDao

    @Singleton
    @Provides
    fun providesSubTaskDao(db: LocalDataBase) = db.subTaskDao

    @Singleton
    @Provides
    fun providesTaskAndSubTaskDao(db: LocalDataBase) = db.taskAndSubTaskDao

    @Singleton
    @Provides   /* The local repository can be changed easily*/
    fun providesLocalTaskRepository(repo: RoomTaskRepository): ILocalTaskRepository = repo
}
