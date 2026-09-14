package com.portocale.volunteer.users

fun User.toUserApi(): UserApi {
    return UserApi(
        id = id,
        name = name,
        age = age
    )
}
