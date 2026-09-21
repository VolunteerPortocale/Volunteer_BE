package com.portocale.volunteer.users

import com.portocale.volunteer.event.EventCategoryApi
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant

@Schema(name = "User")
data class UserApi(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val role: UserRoleApi,
    val status: UserStatusApi,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val eventCategoryPreferences: List<EventCategoryApi>? = null,
    val suspendedUntil: Instant? = null,
)

@Schema(name = "UserRole", enumAsRef = true)
enum class UserRoleApi {
    ADMIN,
    MODERATOR,
    NGO,
    VOLUNTEER
}

@Schema(name = "CreateUserRole", enumAsRef = true)
enum class CreateUserRoleApi {
    MODERATOR,
    NGO,
    VOLUNTEER
}

@Schema(name = "UserStatus", enumAsRef = true)
enum class UserStatusApi {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
}

@Schema(name = "CreateUser")
data class CreateUserApi(
    @field:NotBlank
    val firstName: String,
    @field:NotBlank
    val lastName: String,
    @field:Email
    @field:NotBlank
    val email: String,
    @field:Size(min = 8)
    val password: String,
    @field:NotBlank
    val phoneNumber: String,
    val role: CreateUserRoleApi,
    val eventCategoryPreferences: List<EventCategoryApi>? = null,
)

@Schema(name = "LoginUser")
data class LoginUserApi(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val password: String
)

@Schema(name = "UpdateUser")
data class UpdateUserApi(
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val eventCategoryPreferences: List<EventCategoryApi>? = null
)

@Schema(name = "ValidateRegistrationOtp")
data class ValidateRegistrationOtpApi(
    val email: String,
    val otp: String
)
