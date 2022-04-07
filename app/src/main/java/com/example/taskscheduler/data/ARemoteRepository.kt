package com.example.taskscheduler.data

import com.example.taskscheduler.data.sources.remote.AService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ARemoteRepository @Inject constructor(
    private val api: AService,
) {
    suspend fun getAllFromApi() = api.getAll().toModel()
}
