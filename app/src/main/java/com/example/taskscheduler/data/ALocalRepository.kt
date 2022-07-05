package com.example.taskscheduler.data

import com.example.taskscheduler.domain.models.AModel
import com.example.taskscheduler.domain.models.toEntityWithSubTasks
import com.example.taskscheduler.data.sources.local.dao.ADao
import com.example.taskscheduler.data.sources.local.dao.IALocalRepository
import com.example.taskscheduler.data.sources.local.entities.toModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ALocalRepository @Inject constructor(
    private val aDao: ADao
) : IALocalRepository<AModel> {
    override suspend fun get(key: Int) = aDao.get(key).toModel()

    override suspend fun getAll() = aDao.getAll().toModel()

    override fun getAllLive() = aDao.getAllLive().toModel()


    override suspend fun size() = aDao.size()

    override suspend fun isEmpty() = aDao.isEmpty()


    override suspend fun insert(data: AModel) = aDao.insert(data.toEntity())

    override suspend fun insertAll(data: List<AModel>) = aDao.insertAll(data.toEntityWithSubTasks())


    override suspend fun delete(key: Int) = aDao.delete(key)

    override suspend fun deleteAll() = aDao.deleteAll()


    override suspend fun refresh(data: List<AModel>) = aDao.refresh(data.toEntityWithSubTasks())
}


//@Singleton
//class ALocalRepository @Inject constructor(
//    private val aDao: ADao
//) {
//    suspend fun get(key: Int) = aDao.get(key).toModel()
//
//    suspend fun getAll() = aDao.getAll().toModel()
//
//    fun getAllLive() = aDao.getAllLive().toModel()
//
//
//    suspend fun size() = aDao.size()
//
//    suspend fun isEmpty() = aDao.isEmpty()
//
//
//    suspend fun insert(data: AModel) = aDao.insert(data.toEntity())
//
//    suspend fun insertAll(data: List<AModel>) = aDao.insertAll(data.toEntity())
//
//
//    suspend fun delete(key: Int) = aDao.delete(key)
//
//    suspend fun deleteAll() = aDao.deleteAll()
//
//
//    suspend fun refresh(data: List<AModel>) = aDao.refresh(data.toEntity())
//}
