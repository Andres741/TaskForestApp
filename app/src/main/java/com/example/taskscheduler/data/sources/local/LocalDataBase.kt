package com.example.taskscheduler.data.sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.taskscheduler.data.sources.local.entities.AEntity
import com.example.taskscheduler.data.sources.local.dao.ADao
import com.example.taskscheduler.data.sources.local.dao.SubTaskDao
import com.example.taskscheduler.data.sources.local.dao.TaskDao
import com.example.taskscheduler.data.sources.local.entities.SubTaskEntity
import com.example.taskscheduler.data.sources.local.entities.TaskEntity

@Database(entities = [AEntity::class, TaskEntity::class, SubTaskEntity::class], version = 1, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class LocalDataBase: RoomDatabase() {
    abstract val aDao: ADao
    abstract val taskDao: TaskDao
    abstract val subTaskDao: SubTaskDao
}
