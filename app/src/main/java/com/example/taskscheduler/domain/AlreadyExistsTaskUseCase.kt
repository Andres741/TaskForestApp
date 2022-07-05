package com.example.taskscheduler.domain

import com.example.taskscheduler.domain.models.TaskModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlreadyExistsTaskUseCase @Inject constructor(
    //TODO: Create task repository
) {
    suspend operator fun invoke(titleTask: String): Boolean {
        TODO()
    }
}

