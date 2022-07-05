package com.example.taskscheduler.domain

import com.example.taskscheduler.data.sources.local.dao.TaskDao
import com.example.taskscheduler.domain.models.TaskModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangeDoneStatusOfTaskUseCase @Inject constructor(
    dao: TaskDao
) {
    suspend operator fun invoke(task: TaskModel): Boolean = TODO()
}