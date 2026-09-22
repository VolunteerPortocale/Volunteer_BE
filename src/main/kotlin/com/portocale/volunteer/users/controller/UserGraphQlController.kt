package com.portocale.volunteer.users.controller

import com.portocale.volunteer.config.toLanguageApi
import com.portocale.volunteer.graphql.model.CreateUserInputGQL
import com.portocale.volunteer.graphql.model.UpdateUserInputGQL
import com.portocale.volunteer.graphql.model.UserGQL
import com.portocale.volunteer.users.service.UserService
import com.portocale.volunteer.users.toCreateUserApi
import com.portocale.volunteer.users.toUpdateUserApi
import com.portocale.volunteer.users.toUserGql
import java.time.Instant
import java.util.Locale
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

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

    @MutationMapping
    fun createUser(
        @Argument input: CreateUserInputGQL,
        locale: Locale
    ): UserGQL {
        return userService.selfRegister(input.toCreateUserApi(), locale.toLanguageApi()).toUserGql()
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


