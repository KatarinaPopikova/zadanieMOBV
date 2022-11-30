package sk.stu.fei.mobv.domain

data class Bar(
    val id: Long,
    val name: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    var distance: Double = 0.0,
    var usersCount: Int = 0,
    val phoneNumber: String? = null,
    val webPage: String? = null,
    val ownerName: String? = null
)