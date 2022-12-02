package sk.stu.fei.mobv.network.dtos

import com.squareup.moshi.Json
import sk.stu.fei.mobv.domain.Friend

data class FriendDto(
    @Json(name = "user_id") val id: Long,
    @Json(name = "user_name") val name: String,
    @Json(name = "bar_id") val barId: Long? = null,
    @Json(name = "bar_name") val barName: String? = null,
    @Json(name = "time") val addedTime: String? = null,
    @Json(name = "bar_lat") val barLatitude: Double? = null,
    @Json(name = "bar_lon") val barLongitude: Double? = null
)

fun FriendDto.asDomainModel(): Friend {
    return Friend(
        id = id,
        name = name,
        barId = barId,
        barName = barName,
        addedTime = addedTime,
        barLatitude = barLatitude,
        barLongitude =barLongitude
    )
}

fun List<FriendDto>.asDomainModelList(): List<Friend> {
    return map {
        it.asDomainModel()
    }
}