package com.example.taskscheduler.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.taskscheduler.domain.models.TaskModel
import javax.inject.Inject

class GetTaskPagerBySuperTaskUseCase @Inject constructor(
    //TODO: Create task repository
) {
    operator fun invoke(superTask: TaskModel) = Pager<Int, TaskModel>(
        config = PagingConfig(enablePlaceholders = false, pageSize = PAGE_SIZE),
        pagingSourceFactory = { TODO("Task repositories not implemented") }
    )
}
