package com.example.taskscheduler.data

import com.example.taskscheduler.data.sources.remote.AService
import javax.inject.Inject

class ARemoteRepository @Inject constructor(
    private val api: AService,
) {
    suspend fun getAllFromApi() = api.getAll().toModel()
}
