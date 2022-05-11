package com.example.taskscheduler.data

import com.example.taskscheduler.domain.models.TaskTypeModel
import com.example.taskscheduler.domain.models.allToModel
import com.example.taskscheduler.util.dataStructures.IMultiplicityList
import javax.inject.Inject
import javax.inject.Singleton

/*
    TODO: set the DI
*/
@Singleton
class TaskTypeRepositoryML @Inject constructor(
    /**The keys are the types recognised*/
    private val _taskTypes: IMultiplicityList<String>
) {
    val taskTypes: List<TaskTypeModel> = _taskTypes.allToModel()

    fun addType(type: String) = _taskTypes.insert(type)

    fun isTaskType(possibleType: String) = _taskTypes.contains(possibleType)

    fun multiplicityOf(possibleType: String) = _taskTypes.multiplicityOf(possibleType)
}
