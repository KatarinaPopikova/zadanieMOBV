package sk.stu.fei.mobv.network.dtos

import com.squareup.moshi.Json
import sk.stu.fei.mobv.domain.User

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