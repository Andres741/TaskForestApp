package com.example.taskscheduler.domain

import androidx.lifecycle.LiveData
import com.example.taskscheduler.data.ALocalRepository
import com.example.taskscheduler.data.ARemoteRepository
import com.example.taskscheduler.data.models.AModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAUseCase @Inject constructor(
    private val aLocalRepository: ALocalRepository,
    private val aRemoteRepository: ARemoteRepository,
) {
    suspend operator fun invoke(refreshDB: Boolean = false): LiveData<List<AModel>?> {
        if (aLocalRepository.isEmpty() || refreshDB) {
            val allAPI = aRemoteRepository.getAllFromApi()
            aLocalRepository.refresh(allAPI)
        }
        return aLocalRepository.getAllLive()
    }
}
