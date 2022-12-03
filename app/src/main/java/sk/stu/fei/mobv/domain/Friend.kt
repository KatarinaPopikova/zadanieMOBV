package sk.stu.fei.mobv.domain

import sk.stu.fei.mobv.database.entities.MyFriendEntity

data class Friend(
    val id: Long,
    val name: String,
    val barId: Long? = null,
    val barName: String? = null,
    val addedTime: String? = null,
    val barLatitude: Double? = null,
    val barLongitude: Double? = null
){
    fun asEntityModel(): MyFriendEntity {
        return MyFriendEntity(
            id = id,
            name = name
        )
    }
}
