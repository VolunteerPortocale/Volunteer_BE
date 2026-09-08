package com.example.demo.users.controller

import com.example.demo.users.UserApi
import com.example.demo.users.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
) {
    @GetMapping("/api/v1/users")
    fun getUsers(): List<UserApi> {
        return userService.getAll()
    }
}