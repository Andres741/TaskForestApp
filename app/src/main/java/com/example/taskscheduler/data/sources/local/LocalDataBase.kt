package com.example.taskscheduler.data.sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taskscheduler.data.Converters
import com.example.taskscheduler.data.sources.local.entities.AEntity
import com.example.taskscheduler.data.sources.local.dao.*
import com.example.taskscheduler.data.sources.local.entities.taskEntity.*

@Database(entities = [AEntity::class, TaskEntity::class, SubTaskEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LocalDataBase: RoomDatabase() {
    abstract val aDao: ADao
    abstract val taskDao: TaskDao
    abstract val subTaskDao: SubTaskDao
}
