package com.example.taskscheduler.data.sources.remote.jsons

import com.example.taskscheduler.data.models.AModel
import com.squareup.moshi.Json

//@JsonClass(generateAdapter = true) //Now this doesn't work for some reason.
data class AJsonArray(
    @Json(name = "videos")
    val videos: List<AJson>
) {
    /**
     * Creates an empty JSON array.
     */
    constructor(): this(videos = emptyList())

    fun toModel() = videos.map { it.toModel() }
}

//@JsonClass(generateAdapter = true)
data class AJson(
    @Json(name = "data")
    val data: String,
) {
    fun toModel() = AModel(data = data)
}
