package com.example.taskscheduler.data.sources.local.entities

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskscheduler.data.models.AModel

@Entity(tableName = "a_table")
data class AEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "data")
    val data: String,
) {
    fun toModel() = AModel(data = data)
//    fun toJson() = AJson(data = data)
}

fun List<AEntity>.toModel() = map( AEntity::toModel )
//fun LiveData<List<AEntity>?>.toModel() = Transformations.map(this, List<AEntity>::toModel)
fun LiveData<List<AEntity>?>.toModel() = Transformations.map(this){ it?.toModel() }
