package com.example.demo.users

fun User.toUserApi(): UserApi {
    return UserApi(
        id = id,
        name = name,
        age = age
    )
}