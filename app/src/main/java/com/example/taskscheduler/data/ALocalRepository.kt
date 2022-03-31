package com.example.taskscheduler.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.taskscheduler.data.models.AModel
import com.example.taskscheduler.data.models.toEntity
import com.example.taskscheduler.data.sources.local.dao.ADao
import com.example.taskscheduler.data.sources.local.entities.toModel
import javax.inject.Inject

class ALocalRepository @Inject constructor(
    private val aDao: ADao
) : ALocalRepositoryIF {
    override suspend fun get(key: Int) = aDao.get(key)

    override suspend fun getAll() = aDao.getAll().toModel()

    override fun getAllLive() = Transformations.map(aDao.getAllLive()) {
        it?.toModel()
    }

    override suspend fun insert(data: AModel) = aDao.insert(data.toEntity())

    override suspend fun insertAll(data: List<AModel>) = aDao.insertAll(data.toEntity())


    override suspend fun delete(key: Int) = aDao.delete(key)

    override suspend fun deleteAll() = aDao.deleteAll()


    override suspend fun refresh(data: List<AModel>) = aDao.refresh(data.toEntity())
}

interface ALocalRepositoryIF {

    suspend fun get(key: Int): AModel

    suspend fun getAll(): List<AModel>

    fun getAllLive(): LiveData<List<AModel>?>

    suspend fun insert(data: AModel)

    suspend fun insertAll(data: List<AModel>)

    suspend fun delete(key: Int)

    suspend fun deleteAll()

    suspend fun refresh(data: List<AModel>)
}
