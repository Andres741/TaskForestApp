package com.example.taskscheduler.data.sources.local

import androidx.paging.*
import com.example.taskscheduler.data.sources.local.ILocalTaskRepository.Companion.PAGE_SIZE
import com.example.taskscheduler.data.sources.local.dao.SubTaskDao
import com.example.taskscheduler.data.sources.local.dao.TaskDao
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperAndSubTasks
import com.example.taskscheduler.domain.models.TaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomTaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao,
): ILocalTaskRepository {

    override fun getPagingSource() = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = PAGE_SIZE),
        pagingSourceFactory = { taskDao.getPagingSource() }
    ).flow//.map { it.map(TaskWithSuperAndSubTasks::toModel) } //TODO

    override fun getPagingSourceBySuperTask(superTask: TaskModel) = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = PAGE_SIZE),
        pagingSourceFactory = { taskDao.getPagingSourceBySuperTask(superTask.title) }
    ).flow//.map { it.map(TaskWithSuperAndSubTasks::toModel) }

    override suspend fun existsTitle(taskTitle: String): Boolean = taskDao.containsStatic(taskTitle)

    override suspend fun changeDone(task: TaskModel, newValue: Boolean) = taskDao.changeDone(task.title, newValue)

    override suspend fun saveNewTask(newTask: TaskModel) {
        withContext(Dispatchers.Default) {
            newTask.toEntity().let { taskEntity ->
                taskDao.insert(taskEntity)
            }
            newTask.toSuperTaskEntity()?.let { superTaskEntity ->
                subTaskDao.insert(superTaskEntity)
            }
        }
    }
}
