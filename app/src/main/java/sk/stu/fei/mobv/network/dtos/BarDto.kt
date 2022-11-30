package sk.stu.fei.mobv.network.dtos

import com.squareup.moshi.Json
import sk.stu.fei.mobv.database.entities.BarEntity
import sk.stu.fei.mobv.domain.Bar

data class BarDto(
    @Json(name = "bar_id") val id: Long,
    @Json(name = "bar_name") val name: String,
    @Json(name = "bar_type") val type: String,
    @Json(name = "lat") val latitude: Double,
    @Json(name = "lon") val longitude: Double,
    @Json(name = "users") val usersCount: Int
)

fun BarDto.asDomainModel(): Bar {
    return Bar(
        id = id,
        name = name,
        type = type,
        latitude = latitude,
        longitude = longitude,
        usersCount = usersCount
    )
}

fun List<BarDto>.asDomainModelList(): List<Bar> {
    return map {
        it.asDomainModel()
    }
}

fun BarDto.asEntityModel(): BarEntity {
    return BarEntity(
        id = id,
        name = name,
        type = type,
        latitude = latitude,
        longitude = longitude,
        usersCount = usersCount
    )
}

fun List<BarDto>.asEntityModelList(): List<BarEntity> {
    return map {
        it.asEntityModel()
    }
}