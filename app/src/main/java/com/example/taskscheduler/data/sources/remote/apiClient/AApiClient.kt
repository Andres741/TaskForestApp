package com.example.taskscheduler.data.sources.remote.apiClient

import com.example.taskscheduler.data.sources.remote.jsons.AJsonArray
import retrofit2.Response
import retrofit2.http.GET

abstract class AApiClient {
    @GET("/.json")
    abstract suspend fun getAll(): Response<AJsonArray>
}
