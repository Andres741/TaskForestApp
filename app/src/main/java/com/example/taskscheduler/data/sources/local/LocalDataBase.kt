package com.example.taskscheduler.data.sources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.example.taskscheduler.data.Converters
import com.example.taskscheduler.data.sources.local.entities.AEntity
import com.example.taskscheduler.data.sources.local.dao.*
import com.example.taskscheduler.data.sources.local.entities.taskEntity.*
import com.example.taskscheduler.di.data.LocalModule

@Database(entities = [TaskEntity::class, SubTaskEntity::class], version = 3, exportSchema = true)
//@TypeConverters(Converters::class)
abstract class LocalDataBase: RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val subTaskDao: SubTaskDao
    abstract val taskAndSubTaskDao: TaskAndSubTaskDao

    companion object {
        private const val DATABASE_NAME = "local_database"

        fun build(context: Context) = Room.databaseBuilder(
            context, LocalDataBase::class.java, DATABASE_NAME
        )/*.addTypeConverter(Converters())*/.addMigrations(
            Migration(2,3) {
                //it.execSQL("TODO: delete AEntity table")
            },
            Migration(1,3) {

            }
        ).build()
    }
}
