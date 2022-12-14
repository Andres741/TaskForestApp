package com.example.taskscheduler.util

import androidx.paging.PagingData
import com.example.taskscheduler.domain.models.ITaskTypeNameOwner
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.TaskTypeModel
import kotlinx.coroutines.flow.Flow

typealias TaskDataFlow = Flow<PagingData<TaskModel>>
typealias TaskTypeDataFlow = Flow<PagingData<TaskTypeModel>>
typealias OnClickType = (ITaskTypeNameOwner?) -> Unit
typealias CallbackAndName = Pair<()->Unit, String>
typealias NoMoreWithTaskDeletedType = Boolean
typealias TypeChange = Pair<ITaskTypeNameOwner, ITaskTypeNameOwner>
