package sk.stu.fei.mobv.domain

import com.squareup.moshi.Json

data class Friend(
    val id: Long,
    val name: String,
    val barId: Long? = null,
    val barName: String? = null,
    val addedTime: String? = null,
    val barLatitude: Double? = null,
    val barLongitude: Double? = null
)