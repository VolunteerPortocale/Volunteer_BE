package com.portocale.volunteer.users.controller

import com.portocale.volunteer.graphql.model.UserGQL
import com.portocale.volunteer.users.service.UserService
import com.portocale.volunteer.users.toUserGQL
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserGqlController(
    private val userService: UserService
) {

    @QueryMapping
    fun getAllUsers(): List<UserGQL> {
        return userService.getAll().map { it.toUserGQL() }
    }

    @QueryMapping
    fun getUserById(@Argument id: String): UserGQL {
        return userService.getById(id).toUserGQL()
    }
}


