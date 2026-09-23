package com.portocale.volunteer.users.controller

import org.springframework.web.bind.annotation.RequestParam
import com.portocale.volunteer.users.CreateUserApi
import com.portocale.volunteer.users.LoginUserApi
import com.portocale.volunteer.users.UserApi
import com.portocale.volunteer.users.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
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

    @GetMapping(params = ["email"])
    @Operation(summary = "Get user by email")
    @ApiResponses(
        value = [ApiResponse(
            description = "OK",
            responseCode = "200",
            content = [Content(schema = Schema(implementation = UserApi::class))]
        )]
    )
    fun getByEmail(@RequestParam email: String): UserApi {
        return userService.getByEmail(email)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user")
    fun create(
        @Valid
        @RequestBody input: CreateUserApi
    ): UserApi {
        return userService.create(input)
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    fun login(
        @Valid
        @RequestBody input: LoginUserApi
    ): UserApi {
        return userService.login(input)
    }
}
