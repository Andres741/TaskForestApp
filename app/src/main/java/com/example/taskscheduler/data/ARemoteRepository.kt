package com.example.taskscheduler.data

import com.example.taskscheduler.data.sources.remote.apiClient.AApiClient
import com.example.taskscheduler.data.sources.remote.jsons.AJsonArray
import com.example.taskscheduler.util.returnDefaultIfErrorOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ARemoteRepository @Inject constructor(
    private val api: AApiClient,
) {
//    suspend fun getAllFromApi() = returnDefaultIfErrorOrNull(::AJsonArray, api::getAll ).toModel()
    suspend fun getAllFromApi() = returnDefaultIfErrorOrNull(::AJsonArray){ api.getAll() }.toModel()
}
