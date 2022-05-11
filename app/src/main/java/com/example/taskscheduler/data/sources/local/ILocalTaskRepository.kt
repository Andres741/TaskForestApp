package com.example.taskscheduler.data.sources.local

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperAndSubTasks
import com.example.taskscheduler.domain.models.TaskModel
import kotlinx.coroutines.flow.Flow


interface ILocalTaskRepository {
    fun getPagingSource(): Flow<PagingData<TaskModel>>

    fun getPagingSourceBySuperTask(superTask: TaskModel): Flow<PagingData<TaskModel>>

    suspend fun existsTitle(taskTitle: String): Boolean

    suspend fun changeDone(task: TaskModel, newValue: Boolean)

    suspend fun saveNewTask(newTask: TaskModel)

    companion object {
        const val PAGE_SIZE = 30
    }
}

