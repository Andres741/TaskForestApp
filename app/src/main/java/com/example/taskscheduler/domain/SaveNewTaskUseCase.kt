package com.example.taskscheduler.domain

import com.example.taskscheduler.data.TaskRepository
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class SaveNewTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val createValidTaskUseCase: CreateValidTaskUseCase,
) {

    //TODO: decide which one I will use.
    private val saveTaskContext: CoroutineContext = newSingleThreadContext("saveTaskThread") + NonCancellable
    private val saveTaskContext1: CoroutineContext = Dispatchers.Default.limitedParallelism(1) + NonCancellable
    private val saveTaskContext2: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher() + NonCancellable

    /**
     * This function must be used as the constructor of TaskModel in the IU layer.
     * Is similar to CreateValidTaskUseCase, but it saves the task into the database.
     * All Successful objects returned by this function are instances of SavedTask.
     * This function is synchronized and non cancellable.
     */
    suspend operator fun invoke(
        title: String?, type: String?, description: String?, superTask: String?

    ): CreateValidTaskUseCase.Response = withContext(saveTaskContext2) {

        createValidTaskUseCase(
            title, type, description, superTask
        ).also { response ->
            if (response !is CreateValidTaskUseCase.Response.Successful) return@also

            taskRepository.local.saveNewTask(response.task)
            return@withContext SavedTask(response)
        }
    }
    class SavedTask(successfulResponse: Successful): CreateValidTaskUseCase.Response.Successful(successfulResponse.task)
}

//final int sizeArr = 7
//int* myArray = new int[sizeArr];
//int primerNum = myArray[0];
//int segNum = (myArray+1)*;
//int terNum = (myArray+2)*;
//int quintNum = myArray[4];
//int sepNum = myArray[6];
//int noMyNum = myArray[77];
//myArray[77] = 77;
//delete myArray;
