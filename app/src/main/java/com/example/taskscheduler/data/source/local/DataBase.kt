package com.example.taskscheduler.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.taskscheduler.data.ADataClass

@Database(entities = [ADataClass::class], version = 1, exportSchema = false)
abstract class DataBase: RoomDatabase(){
    abstract fun aDao(): ADao
}

