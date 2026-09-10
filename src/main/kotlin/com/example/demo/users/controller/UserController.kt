package com.example.demo.users.controller

import com.example.demo.users.UserApi
import com.example.demo.users.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    fun getAll(): List<UserApi> {
        return userService.getAll()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): UserApi {
        return userService.getById(id)
    }
}