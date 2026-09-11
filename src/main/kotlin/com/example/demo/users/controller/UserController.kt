package com.example.demo.users.controller

import com.example.demo.users.UserApi
import com.example.demo.users.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Users", description = "Group of endpoints for user management")
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    @Operation(summary = "Get all users")
    fun getAll(): List<UserApi> {
        return userService.getAll()
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by Id")
    @ApiResponses(
        value = [ApiResponse(
            description = "OK",
            responseCode = "200",
            content = [Content(schema = Schema(implementation = UserApi::class))]
        )]
    )
    fun getById(@PathVariable id: String): UserApi {
        return userService.getById(id)
    }
}
