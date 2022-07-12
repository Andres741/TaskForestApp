package com.example.taskscheduler.di.data

import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.data.sources.local.ILocalTaskRepository
import com.example.taskscheduler.data.sources.local.ITaskRepository
import com.example.taskscheduler.data.sources.remote.firestore.FirestoreTasksAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object JoinModule {

    @Singleton //Will be a problem?
    @Provides
    fun providesTaskRepository(
        local: ILocalTaskRepository,
        firestoreTasksAuth: FirestoreTasksAuth,
    ): ITaskRepository {
        return firestoreTasksAuth.firestoreTasks?.run {
            TaskRepository(local, this)
        } ?: local
    }
}
