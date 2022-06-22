package com.example.taskscheduler.util

import androidx.paging.PagingData
import com.example.taskscheduler.domain.models.TaskModel
import com.example.taskscheduler.domain.models.TaskTypeModel
import kotlinx.coroutines.flow.Flow

typealias TaskDataFlow = Flow<PagingData<TaskModel>>
typealias TaskTypeDataFlow = Flow<PagingData<TaskTypeModel>>
typealias OnClickTaskTypeVH = (TaskTypeModel?) -> Unit
typealias CallbackAndName = Pair<()->Unit, String>

