package com.example.taskscheduler.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.taskscheduler.domain.models.TaskModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTaskPagerUseCase @Inject constructor(
    //TODO: Create task repository
) {
    operator fun invoke() = Pager<Int, TaskModel>(
        config = PagingConfig(enablePlaceholders = false, pageSize = PAGE_SIZE),
        pagingSourceFactory = { TODO("Task repositories not implemented") }
    )
}

const val PAGE_SIZE = 30

