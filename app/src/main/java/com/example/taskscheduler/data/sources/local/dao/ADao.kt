package com.example.taskscheduler.data.sources.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskscheduler.data.sources.local.entities.AEntity

//@Dao
//interface ADao: IALocalRepository<AEntity> {
//    // Get
//    @Query("SELECT * FROM a_table WHERE id = :key")
//    override suspend fun get(key: Int): AEntity
//
//    @Query("SELECT * FROM a_table")
//    override suspend fun getAll(): List<AEntity>
//
//    @Query("SELECT * FROM a_table")
//    override fun getAllLive(): LiveData<List<AEntity>?>
//
//
//    @Query("SELECT COUNT(id) FROM a_table")
//    override suspend fun size(): Int
//
//    //    suspend fun isEmpty() = size() == 0
//    @Query("SELECT EXISTS(SELECT id FROM a_table LIMIT 1)")
//    override suspend fun isEmpty(): Boolean
//
//
//    //Insert
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    override suspend fun insert(data: AEntity)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    override suspend fun insertAll(data: List<AEntity>)
//
//    //Delete
//    @Query("DELETE FROM a_table WHERE id = :key")
//    override suspend fun delete(key: Int)
//
//    @Query("DELETE FROM a_table")
//    override suspend fun deleteAll()
//
//}

interface IALocalRepository<T> {

    suspend fun get(key: Int): T

    suspend fun getAll(): List<T>  //?

    fun getAllLive(): LiveData<List<T>?>

    suspend fun size(): Int

    suspend fun isEmpty(): Boolean

    suspend fun insert(data: T)  //?

    suspend fun insertAll(data: List<T>)  //?

    suspend fun delete(key: Int)  //?

    suspend fun deleteAll()  //?


    suspend fun refresh(data: List<T>) {  //?
        deleteAll()
        insertAll(data)
    }
}


@Dao
interface ADao {
    // Get
    @Query("SELECT * FROM a_table WHERE id = :key")
    suspend fun get(key: Int): AEntity

    @Query("SELECT * FROM a_table")
    suspend fun getAll(): List<AEntity>

    @Query("SELECT * FROM a_table")
    fun getAllLive(): LiveData<List<AEntity>?>


    @Query("SELECT COUNT(id) FROM a_table")
    suspend fun size(): Int

    //    suspend fun isEmpty() = size() == 0
    @Query("SELECT EXISTS(SELECT id FROM a_table LIMIT 1)")
    suspend fun isEmpty(): Boolean


    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: AEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<AEntity>)

    //Delete
    @Query("DELETE FROM a_table WHERE id = :key")
    suspend fun delete(key: Int)

    @Query("DELETE FROM a_table")
    suspend fun deleteAll()

    suspend fun refresh(data: List<AEntity>) {
        deleteAll()
        insertAll(data)
    }
}
