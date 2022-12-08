package sk.stu.fei.mobv.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import sk.stu.fei.mobv.domain.Bar

@Entity(tableName = "bars")
class BarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "users_count")
    var usersCount: Int
)

fun BarEntity.asDomainModel(): Bar {
    return Bar(
        id = id,
        name = name,
        type = type,
        latitude = latitude,
        longitude = longitude,
        usersCount = usersCount
    )
}

fun List<BarEntity>.asDomainModelList(): List<Bar> {
    return map {
        it.asDomainModel()
    }
}