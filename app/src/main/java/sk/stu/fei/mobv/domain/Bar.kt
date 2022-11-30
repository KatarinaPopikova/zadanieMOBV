package sk.stu.fei.mobv.domain

data class Bar(
    val id: Long,
    val name: String,
    val type: String,
    val latitude: Double,
    var longitude: Double,
    var usersCount: Int
)