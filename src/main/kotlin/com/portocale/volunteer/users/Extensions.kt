package com.portocale.volunteer.users

import com.portocale.volunteer.graphql.model.UserGQL

fun User.toUserApi(): UserApi {
    return UserApi(
        id = id,
        name = name,
        age = age
    )
}

fun UserApi.toUserGQL(): UserGQL {
    return UserGQL(
        id,
        name,
        age
    )
}

fun User.toUserGQL(): UserGQL {
    return UserGQL(
        id,
        name,
        age
    )
}

