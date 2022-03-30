package com.example.taskscheduler.data.sources.remote

import com.example.taskscheduler.data.sources.remote.apiClient.AApiClient
import com.example.taskscheduler.data.sources.remote.jsons.AJson
import com.example.taskscheduler.data.sources.remote.jsons.AJsonArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class AService @Inject constructor(private val api: AApiClient) {

    suspend fun getAll(): AJsonArray {
        return withContext(Dispatchers.IO) {
            try {
                api.getAll().body()?: AJsonArray()
            } catch (e: Exception) {
                AJsonArray()
            }
        }
    }
}
