package com.portocale.volunteer.users.controller

import com.portocale.volunteer.config.jwt.Principal
import com.portocale.volunteer.config.toLanguageApi
import com.portocale.volunteer.graphql.model.CreateUserInputGQL
import com.portocale.volunteer.graphql.model.UpdateUserInputGQL
import com.portocale.volunteer.graphql.model.UserGQL
import com.portocale.volunteer.users.UserApi
import com.portocale.volunteer.users.service.UserService
import com.portocale.volunteer.users.toCreateUserApi
import com.portocale.volunteer.users.toLanguageApi
import com.portocale.volunteer.users.toUpdateUserApi
import com.portocale.volunteer.users.toUserGql
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import java.time.Instant
import java.util.Locale
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UserGraphQlController(
    private val userService: UserService
) {

    @QueryMapping
    fun getAllUsers(): List<UserGQL> {
        return userService.getAll().map { it.toUserGql() }
    }

    @QueryMapping
    fun getUserById(@Argument id: String): UserGQL {
        return userService.getById(id).toUserGql()
    }

    @QueryMapping
    fun getCurrentUser(principal: Principal): UserApi {
        return userService.getById(principal.userId)
    }

    @QueryMapping
    fun isEmailRegistered(@Argument("email") email: String): Boolean {
        return userService.isEmailRegistered(email)
    }

    @MutationMapping
    fun createUser(
        @Argument input: CreateUserInputGQL,
        locale: Locale
    ): UserGQL {
        val preferredLanguage = input.language?.toLanguageApi() ?: locale.toLanguageApi()
        val createApi = input.toCreateUserApi().copy(language = preferredLanguage)
        return userService.selfRegister(createApi, preferredLanguage).toUserGql()
    }

    @MutationMapping
    fun validateRegistrationOtp(
        @Argument email: String,
        @Argument otp: String
    ): UserGQL {
        return userService
            .validateRegistrationOtp(
                email = email,
                otp = otp
            )
            .toUserGql()
    }

    @MutationMapping
    fun resendRegistrationOtp(
        @Argument email: String,
        locale: Locale
    ): Boolean {
        userService.resendRegistrationOtp(email, locale.toLanguageApi())
        return true
    }

    @MutationMapping
    fun updateUser(
        @Argument input: UpdateUserInputGQL
    ): UserGQL {
        return userService.update(
            input = input.toUpdateUserApi()
        ).toUserGql()
    }

    @MutationMapping
    fun suspendUser(
        @Argument id: String,
        @Argument suspendedUntil: String
    ): UserGQL {
        return userService.suspend(
            id = id,
            suspendedUntil = Instant.parse(suspendedUntil)
        ).toUserGql()
    }

    @MutationMapping
    fun deleteUser(
        @Argument id: String
    ): Boolean {
        return userService.delete(id)
    }
}

