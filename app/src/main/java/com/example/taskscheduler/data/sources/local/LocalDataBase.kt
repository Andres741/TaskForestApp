package com.example.taskscheduler.data.sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.taskscheduler.data.sources.local.entities.AEntity
import com.example.taskscheduler.data.sources.local.dao.ADao

@Database(entities = [AEntity::class], version = 1, exportSchema = false)
abstract class LocalDataBase: RoomDatabase() {
    abstract val aDao: ADao
}
