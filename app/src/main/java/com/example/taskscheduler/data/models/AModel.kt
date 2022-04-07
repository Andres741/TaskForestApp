package com.example.taskscheduler.data.models

import com.example.taskscheduler.data.sources.local.entities.AEntity
import com.example.taskscheduler.data.sources.remote.jsons.AJson
import com.example.taskscheduler.data.sources.remote.jsons.AJsonArray

//import com.example.taskscheduler.data.sources.remote.jsons.AJson
//import com.example.taskscheduler.data.sources.remote.jsons.AJsonArray

data class AModel (
    val data: String,
) {
    fun toEntity() = AEntity(data = data)
    fun toJson() = AJson(data = data)
}

fun List<AModel>.toEntity() = map { it.toEntity() }
fun List<AModel>.toJSonArray() = AJsonArray( map { it.toJson() } )
