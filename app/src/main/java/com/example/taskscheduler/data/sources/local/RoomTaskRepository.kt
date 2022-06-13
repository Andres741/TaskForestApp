package com.example.taskscheduler.data.sources.local

import androidx.paging.*
import androidx.room.Transaction
import com.example.taskscheduler.data.sources.local.ILocalTaskRepository.Companion.PAGE_SIZE
import com.example.taskscheduler.data.sources.local.dao.SubTaskDao
import com.example.taskscheduler.data.sources.local.dao.TaskDao
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperAndSubTasks
import com.example.taskscheduler.data.sources.local.entities.taskEntity.TaskWithSuperTask
import com.example.taskscheduler.domain.models.TaskModel
import kotlinx.coroutines.flow.*
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
    ).flow.map { it.map (TaskWithSuperAndSubTasks::toModel) }

    override fun getPagingSourceBySuperTask(superTask: TaskModel) = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = PAGE_SIZE),
        pagingSourceFactory = { taskDao.getPagingSourceBySuperTask(superTask.title) }
    ).flow.map { it.map (TaskWithSuperAndSubTasks::toModel) }

    override suspend fun existsTitle(taskTitle: String): Boolean = taskDao.containsStatic(taskTitle)

    override suspend fun changeDone(task: TaskModel, newValue: Boolean) = taskDao
        .changeDone(task.title, newValue) > 0

    @Transaction
    override suspend fun saveNewTask(newTask: TaskModel) {
        newTask.toEntity().let { taskEntity ->
            taskDao.insert(taskEntity)
        }
        newTask.toSuperTaskEntity()?.let { superTaskEntity ->
            subTaskDao.insert(superTaskEntity)
        }
    }

    override fun getAllTasks(): Flow<List<TaskModel>> = taskDao
        .getAllTasksWithSuperAndSubTasks().map {
            it.map (TaskWithSuperAndSubTasks::toModel)
        }

    override suspend fun getAllTasksStatic(): List<TaskModel> = taskDao
        .getAllTasksWithSuperAndSubTasksStatic().map (TaskWithSuperAndSubTasks::toModel)

    override fun getTaskByTitle(title: String): Flow<TaskModel> = taskDao.
        getTaskWithSuperTask(title).map(TaskWithSuperTask::toModel)

    override suspend fun getTaskByTitleStatic(title: String) = taskDao.
        getTaskWithSuperTaskStatic(title).toModel()

    override suspend fun getTaskTypeByTitleStatic(title: String): String = taskDao.
        getTypeStatic(title)



//    private fun<T> T.log(msj: String? = null) = apply {
//        Log.i(
//            "RoomTaskRepository",
//            "${if (msj != null) "$msj: " else ""}${toString()}"
//        )
//    }
}
