package sk.stu.fei.mobv.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import sk.stu.fei.mobv.domain.Friend

@Entity(tableName = "my_friends")
class MyFriendEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,
)

fun MyFriendEntity.asDomainModel(): Friend {
    return Friend(
        id = id,
        name = name
    )
}

fun List<MyFriendEntity>.asDomainModelList(): List<Friend> {
    return map {
        it.asDomainModel()
    }
}