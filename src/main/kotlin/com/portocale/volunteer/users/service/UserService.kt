package com.portocale.volunteer.users.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.users.CreateUserApi
import com.portocale.volunteer.users.LoginUserApi
import com.portocale.volunteer.users.UpdateUserApi
import com.portocale.volunteer.users.UserApi
import java.time.Instant
import java.util.Locale
import org.springframework.security.access.prepost.PreAuthorize


@SuppressWarnings("TooManyFunctions")
interface UserService {

    @PreAuthorize("hasRole('ADMIN')")
    fun getAll(): List<UserApi>

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    fun getById(id: String): UserApi

    @PreAuthorize("hasRole('ADMIN')")
    fun getByEmail(email: String): UserApi

    @PreAuthorize("hasRole('ADMIN')")
    fun create(input: CreateUserApi): UserApi

    @PreAuthorize("hasAnyRole('ADMIN')")
    fun update(
        id: String,
        input: UpdateUserApi,
    ): UserApi

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'NGO', 'VOLUNTEER')")
    fun update(
        input: UpdateUserApi,
    ): UserApi

    @PreAuthorize("hasRole('ADMIN')")
    fun suspend(
        id: String,
        suspendedUntil: Instant,
    ): UserApi

    @PreAuthorize("hasRole('ADMIN')")
    fun delete(id: String): Boolean

    //    Unprotected services
    fun selfRegister(input: CreateUserApi, language: LanguageApi): UserApi

    fun validateRegistrationOtp(
        email: String,
        otp: String
    ): UserApi

    fun resendRegistrationOtp(
        email: String,
        language: LanguageApi
    )

    fun login(input: LoginUserApi): UserApi

    fun isEmailRegistered(email: String): Boolean

}
