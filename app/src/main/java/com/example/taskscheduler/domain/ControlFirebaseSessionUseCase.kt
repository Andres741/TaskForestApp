package com.example.taskscheduler.domain

import com.example.taskscheduler.data.FirestoreSynchronizedTaskRepository
import com.example.taskscheduler.data.sources.local.ITaskRepository
import javax.inject.Inject

class ControlFirebaseSessionUseCase (
    private val taskRepository: FirestoreSynchronizedTaskRepository,
) {
    fun finish() {
        taskRepository.onSessionFinish()
    }
    fun start() {
        taskRepository.onSessionStarts()
    }
}

class FinishFirebaseSessionUseCaseAuth @Inject constructor(
    taskRepository: ITaskRepository,
) {
    val value = (taskRepository as? FirestoreSynchronizedTaskRepository)?.let { repo ->
        ControlFirebaseSessionUseCase(repo)
    }
}
