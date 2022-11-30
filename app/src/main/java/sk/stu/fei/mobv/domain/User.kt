package sk.stu.fei.mobv.domain

import sk.stu.fei.mobv.network.dtos.UserDto

data class User(
    val id: String,
    val access: String,
    val refresh: String
)