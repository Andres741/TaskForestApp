package com.example.taskscheduler.data.sources.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskscheduler.data.ALocalRepositoryIF
import com.example.taskscheduler.data.models.AModel
import com.example.taskscheduler.data.sources.local.entities.AEntity

@Dao
interface ADao : ALocalRepositoryIF<AEntity, AEntity>
{
    // Get
    @Query("SELECT * FROM a_table WHERE id = :key")
    override suspend fun get(key: Int): AEntity

    @Query("SELECT * FROM a_table")
    override suspend fun getAll(): List<AEntity>

    @Query("SELECT * FROM a_table")
    override fun getAllLive(): LiveData<List<AEntity>?>

    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(data: AEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertAll(list: List<AEntity>)

    //Delete
    @Query("DELETE FROM a_table WHERE id = :key")
    override suspend fun delete(key: Int)

    @Query("DELETE FROM a_table")
    override suspend fun deleteAll()

    override suspend fun refresh(data: List<AEntity>) {
        deleteAll()
        insertAll(data)
    }
}
