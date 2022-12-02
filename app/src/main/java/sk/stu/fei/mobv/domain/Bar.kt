package sk.stu.fei.mobv.domain

import android.location.Location

data class Bar(
    val id: Long,
    val name: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    var distance: Double? = null,
    var usersCount: Int? = null,
    val phoneNumber: String? = null,
    val webPage: String? = null,
    val ownerName: String? = null
) {

    fun getDistanceTo(latitude: Double, longitude: Double): Double {
        return Location("").apply {
            this.latitude = this@Bar.latitude
            this.longitude = this@Bar.longitude
        }.distanceTo(Location("").apply {
            this.latitude = latitude
            this.longitude = longitude
        }).toDouble()
    }
}

