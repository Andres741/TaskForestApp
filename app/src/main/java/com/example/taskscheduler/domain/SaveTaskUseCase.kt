package com.example.taskscheduler.domain

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveTaskUseCase @Inject constructor(
    //TODO: Create task repository
    private val createValidTaskUseCase: CreateValidTaskUseCase,
) {
    suspend operator fun invoke(title: String?, type: String?, description: String?, superTask: String?): Boolean {
        createValidTaskUseCase(title, type, description, superTask)?.let { nexTask ->
            TODO("Save the task in the database using a repository.")

            return true
        }
        return false
    }
}

