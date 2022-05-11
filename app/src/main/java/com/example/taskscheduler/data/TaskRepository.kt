package com.example.taskscheduler.data

import com.example.taskscheduler.data.sources.local.ILocalTaskRepository
import com.example.taskscheduler.data.sources.local.dao.ADao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    val local: ILocalTaskRepository
    //TODO: crate remote repository
) {
}
