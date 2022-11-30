package sk.stu.fei.mobv.network.dtos

import com.squareup.moshi.Json
import sk.stu.fei.mobv.domain.Bar

data class TagBarContainerDto(
    @Json(name = "elements") val tagBarList: List<TagBarDto>
)

data class TagBarDto(
    @Json(name = "id") val id: Long,
    @Json(name = "lat") val latitude: Double,
    @Json(name = "lon") val longitude: Double,
    @Json(name = "tags") val tags: TagBarTagDto
)

data class TagBarTagDto(
    @Json(name = "name") val name: String?,
    @Json(name = "amenity") val type: String?,
    @Json(name = "operator") val ownerName: String?,
    @Json(name = "phone") val phoneNumber: String?,
    @Json(name = "website") val webPage: String?,
)


fun TagBarDto.asDomainModel(): Bar {
    return Bar(
        id = id,
        name = tags.name ?: "",
        type = tags.type ?: "",
        latitude = latitude,
        longitude = longitude,
        ownerName = tags.ownerName,
        phoneNumber = tags.phoneNumber,
        webPage = tags.webPage
    )
}

