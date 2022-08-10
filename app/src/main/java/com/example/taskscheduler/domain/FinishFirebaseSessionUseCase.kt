package com.example.taskscheduler.domain

import com.example.taskscheduler.data.FirestoreSynchronizedTaskRepository
import com.example.taskscheduler.data.sources.local.ITaskRepository
import javax.inject.Inject

class FinishFirebaseSessionUseCase (
    private val taskRepository: FirestoreSynchronizedTaskRepository,
) {
    operator fun invoke() {
        taskRepository.onSessionFinish()
    }
}

class FinishFirebaseSessionUseCaseAuth @Inject constructor(
    taskRepository: ITaskRepository,
) {
    val value = (taskRepository as? FirestoreSynchronizedTaskRepository)?.let { repo ->
        FinishFirebaseSessionUseCase(repo)
    }
}
