package com.portocale.volunteer.users.controller

import com.portocale.volunteer.users.UserApi
import com.portocale.volunteer.users.service.UserService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserGraphQlController(
    private val userService: UserService
) {

    @QueryMapping
    fun getAllUsers(): List<UserApi> {
        return userService.getAll()
    }

    @QueryMapping
    fun getUserById(@Argument id: String): UserApi {
        return userService.getById(id)
    }
}

