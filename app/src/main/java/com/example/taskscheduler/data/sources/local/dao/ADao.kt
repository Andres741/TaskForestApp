package com.example.taskscheduler.data.sources.local.dao

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import androidx.room.*
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
    private companion object {
        const val table = "a_table"
    }

    // Get
    @Query("SELECT * FROM $table WHERE id = :key")
    suspend fun get(key: Int): AEntity

    @Query("SELECT * FROM $table")
    suspend fun getAll(): List<AEntity>

    @Query("SELECT * FROM $table")
    fun getAllLive(): LiveData<List<AEntity>?>


    @Query("SELECT COUNT(*) FROM $table")
    suspend fun size(): Int

    //    suspend fun isEmpty() = size() == 0
    @Query("SELECT EXISTS(SELECT id FROM $table LIMIT 1)")
    suspend fun isEmpty(): Boolean


    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: AEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: Iterable<AEntity>)

    //Delete
    @Query("DELETE FROM $table WHERE id = :key")
    suspend fun delete(key: Int)

    @Query("DELETE FROM $table")
    suspend fun deleteAll()

    @Transaction
    suspend fun refresh(data: List<AEntity>) {
        deleteAll()
        insertAll(data)
    }
}
