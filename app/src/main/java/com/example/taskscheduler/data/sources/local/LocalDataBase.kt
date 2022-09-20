package com.example.taskscheduler.data.sources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.example.taskscheduler.data.sources.local.dao.*
import com.example.taskscheduler.data.sources.local.entities.taskEntity.*

@Database(entities = [TaskEntity::class, SubTaskEntity::class], version = 4, exportSchema = true)
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
            Migration(1,2) {},
            Migration(1,3) { db ->
                db.execSQL("DROP TABLE IF EXISTS a_table")
            },
            Migration(2,3) { db ->
                db.execSQL("DROP TABLE IF EXISTS a_table")
            },
            Migration(3, 4) { db ->
                db.execSQL("ALTER TABLE $TASK_TABLE ADD COLUMN $ADVISE_DATEa INTEGER DEFAULT NULL")
            },
        ).build()
    }
}
