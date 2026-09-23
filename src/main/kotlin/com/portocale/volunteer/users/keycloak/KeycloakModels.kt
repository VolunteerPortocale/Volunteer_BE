package com.portocale.volunteer.users.keycloak

import com.portocale.volunteer.users.User
import com.portocale.volunteer.users.UserStatus
import java.time.Instant

data class KcUserApi(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val enabled: Boolean,
    val emailVerified: Boolean,
    val roles: List<String>,
)

data class KcVerifyRequest(val username: String, val password: String)

data class KcVerifyResponse(val valid: Boolean)

fun User.isLoginAllowed(now: Instant = Instant.now()): Boolean = when (status) {
    UserStatus.ACTIVE -> true
    UserStatus.INACTIVE -> false
    UserStatus.SUSPENDED -> suspendedUntil?.isBefore(now) ?: false
}

fun User.toKcUserApi(): KcUserApi = KcUserApi(
    id = id ?: error("Missing user id"),
    username = email,
    email = email,
    firstName = firstName,
    lastName = lastName,
    enabled = isLoginAllowed(),
    emailVerified = status != UserStatus.INACTIVE,
    roles = listOf(role.name),
)
