package sk.stu.fei.mobv.network.dtos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import sk.stu.fei.mobv.domain.User

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "uid") val id: String,
    @Json(name = "access") val access: String,
    @Json(name = "refresh") val refresh: String
)

fun UserDto.asDomainModel(): User {
    return User(
        id = id,
        refresh = refresh,
        access = access
    )
}