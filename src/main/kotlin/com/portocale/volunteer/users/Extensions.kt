@file:Suppress("TooManyFunctions", "CyclomaticComplexMethod")

package com.portocale.volunteer.users

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.event.EventCategoryApi
import com.portocale.volunteer.event.toEventCategory
import com.portocale.volunteer.event.toEventCategoryApi
import com.portocale.volunteer.graphql.model.CreateUserInputGQL
import com.portocale.volunteer.graphql.model.CreateUserRoleGQL
import com.portocale.volunteer.graphql.model.EventCategoryGQL
import com.portocale.volunteer.graphql.model.LanguageGQL
import com.portocale.volunteer.graphql.model.UpdateUserInputGQL
import com.portocale.volunteer.graphql.model.UserGQL
import com.portocale.volunteer.graphql.model.UserRoleGQL
import com.portocale.volunteer.graphql.model.UserStatusGQL
import java.time.Duration
import java.time.Instant
import javax.naming.directory.InvalidAttributesException
import org.springframework.security.crypto.password.PasswordEncoder

// *** API extensions ***
fun User.toUserApi(): UserApi {
    return UserApi(
        id = id ?: error(UserClassCastException("Missing user id")),
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
        role = role.toUserRoleApi(),
        status = status.toUserStatusApi(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        eventCategoryPreferences = eventCategoryPreferences?.map { it.toEventCategoryApi() },
        suspendedUntil = suspendedUntil,
        language = language,
        notificationsEnabled = notificationsEnabled,
        forceResetPassword = forceResetPassword,
        twoFactorEnabled = twoFactorEnabled,
        biography = biography
    )
}

fun UserStatus.toUserStatusApi(): UserStatusApi {
    return when (this) {
        UserStatus.ACTIVE -> UserStatusApi.ACTIVE
        UserStatus.INACTIVE -> UserStatusApi.INACTIVE
        UserStatus.SUSPENDED -> UserStatusApi.SUSPENDED
    }
}

fun UserRole.toUserRoleApi(): UserRoleApi {
    return when (this) {
        UserRole.ADMIN -> UserRoleApi.ADMIN
        UserRole.VOLUNTEER -> UserRoleApi.VOLUNTEER
        UserRole.NGO -> UserRoleApi.NGO
        UserRole.MODERATOR -> UserRoleApi.MODERATOR
    }
}

fun CreateUserInputGQL.toCreateUserApi(): CreateUserApi {
    return CreateUserApi(
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
        role = role.toCreateUserRoleApi(),
        eventCategoryPreferences = eventCategoryPreferences?.map { it.toEventCategoryApi() },
        password = password,
        companyName = companyName,
        language = language?.toLanguageApi() ?: LanguageApi.RO,
        notificationsEnabled = notificationsEnabled ?: true,
        biography = biography
    )
}

fun UpdateUserInputGQL.toUpdateUserApi(): UpdateUserApi {
    return UpdateUserApi(
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        eventCategoryPreferences = eventCategoryPreferences?.map {
            it.toEventCategoryApi()
        },
        companyName = companyName,
        language = language?.toLanguageApi(),
        notificationsEnabled = notificationsEnabled,
        biography = biography,
        twoFactorEnabled = twoFactorEnabled
    )
}

fun LanguageGQL.toLanguageApi(): LanguageApi {
    return when (this) {
        LanguageGQL.RO -> LanguageApi.RO
        LanguageGQL.EN -> LanguageApi.EN
        LanguageGQL.RU -> LanguageApi.RU
    }
}

fun UserStatusGQL.toUserStatusApi(): UserStatusApi {
    return when (this) {
        UserStatusGQL.ACTIVE -> UserStatusApi.ACTIVE
        UserStatusGQL.INACTIVE -> UserStatusApi.INACTIVE
        UserStatusGQL.SUSPENDED -> UserStatusApi.SUSPENDED
    }
}

fun UserRoleGQL.toUserRoleApi(): UserRoleApi {
    return when (this) {
        UserRoleGQL.ADMIN -> UserRoleApi.ADMIN
        UserRoleGQL.MODERATOR -> UserRoleApi.MODERATOR
        UserRoleGQL.VOLUNTEER -> UserRoleApi.VOLUNTEER
        UserRoleGQL.NGO -> UserRoleApi.NGO
    }
}

fun CreateUserRoleGQL.toCreateUserRoleApi(): CreateUserRoleApi {
    return when (this) {
        CreateUserRoleGQL.MODERATOR -> CreateUserRoleApi.MODERATOR
        CreateUserRoleGQL.NGO -> CreateUserRoleApi.NGO
        CreateUserRoleGQL.VOLUNTEER -> CreateUserRoleApi.VOLUNTEER
    }
}

fun EventCategoryGQL.toEventCategoryApi(): EventCategoryApi {
    return when (this) {
        EventCategoryGQL.SOCIAL -> EventCategoryApi.SOCIAL
        EventCategoryGQL.ANIMAL_CARE -> EventCategoryApi.ANIMAL_CARE
        EventCategoryGQL.CHILDREN_AND_YOUTH -> EventCategoryApi.CHILDREN_AND_YOUTH
        EventCategoryGQL.EDUCATION -> EventCategoryApi.EDUCATION
        EventCategoryGQL.ENVIRONMENT -> EventCategoryApi.ENVIRONMENT
        EventCategoryGQL.HEALTH -> EventCategoryApi.HEALTH
        EventCategoryGQL.DISABILITY_SUPPORT -> EventCategoryApi.DISABILITY_SUPPORT
        EventCategoryGQL.ELDERLY_CARE -> EventCategoryApi.ELDERLY_CARE
        EventCategoryGQL.DISASTER -> EventCategoryApi.DISASTER
        EventCategoryGQL.POVERTY -> EventCategoryApi.POVERTY
        EventCategoryGQL.CULTURE -> EventCategoryApi.CULTURE
        EventCategoryGQL.SPORT -> EventCategoryApi.SPORT
        EventCategoryGQL.FESTIVALS -> EventCategoryApi.FESTIVALS
        EventCategoryGQL.TECHNOLOGY -> EventCategoryApi.TECHNOLOGY
        EventCategoryGQL.BUSINESS -> EventCategoryApi.BUSINESS
        EventCategoryGQL.EMPLOYMENT -> EventCategoryApi.EMPLOYMENT
        EventCategoryGQL.SCIENCE -> EventCategoryApi.SCIENCE
        EventCategoryGQL.AGRICULTURE -> EventCategoryApi.AGRICULTURE
        EventCategoryGQL.CONSTRUCTION -> EventCategoryApi.CONSTRUCTION
        EventCategoryGQL.RELIGION -> EventCategoryApi.RELIGION
        EventCategoryGQL.HUMAN_RIGHTS -> EventCategoryApi.HUMAN_RIGHTS
        EventCategoryGQL.LEGAL -> EventCategoryApi.LEGAL
        EventCategoryGQL.SAFETY -> EventCategoryApi.SAFETY
        EventCategoryGQL.FAMILY -> EventCategoryApi.FAMILY
        EventCategoryGQL.LGBTQ_PLUS -> EventCategoryApi.LGBTQ_PLUS
        EventCategoryGQL.REFUGEE_SUPPORT -> EventCategoryApi.REFUGEE_SUPPORT
        EventCategoryGQL.INTERNATIONAL_VOLUNTEERING -> EventCategoryApi.INTERNATIONAL_VOLUNTEERING
        EventCategoryGQL.TOURISM -> EventCategoryApi.TOURISM
        EventCategoryGQL.HERITAGE -> EventCategoryApi.HERITAGE
        EventCategoryGQL.MEDIA -> EventCategoryApi.MEDIA
        EventCategoryGQL.GARDENING -> EventCategoryApi.GARDENING
        EventCategoryGQL.PEACE -> EventCategoryApi.PEACE
        EventCategoryGQL.ADDICTION_RECOVERY -> EventCategoryApi.ADDICTION_RECOVERY
        EventCategoryGQL.OTHER -> EventCategoryApi.OTHER
    }
}

// *** Entity extensions ***
private val REGISTRATION_VALIDITY = Duration.ofDays(1)

fun CreateUserApi.toUser(
    passwordEncoder: PasswordEncoder,
    isSelfRegistered: Boolean = false,
    otp: String? = null
): User {
    val now = Instant.now()

    if (isSelfRegistered && otp == null) {
        throw InvalidAttributesException(
            "OTP is required for self registration"
        )
    }

    if (biography != null && biography.length > MAX_BIOGRAPHY_LENGTH) {
        throw UserInvalidBiographyException(
            "Biography must not exceed $MAX_BIOGRAPHY_LENGTH characters"
        )
    }

    return User(
        firstName = firstName,
        lastName = lastName,
        email = email.trim().lowercase(),
        phoneNumber = phoneNumber,
        role = role.toUserRole(),
        status = if (isSelfRegistered) {
            UserStatus.INACTIVE
        } else {
            UserStatus.ACTIVE
        },
        eventCategoryPreferences =
            eventCategoryPreferences?.map {
                it.toEventCategory()
            },
        companyName = companyName,
        biography = biography,
        createdAt = now,
        language = language,
        notificationsEnabled = notificationsEnabled,
        forceResetPassword = false,
        passwordHash = passwordEncoder.encode(password) ?: error(IllegalStateException("Password is empty")),
        registrationOtpHash = if (isSelfRegistered) {
            passwordEncoder.encode(otp!!)
        } else {
            null
        },
        registrationExpiresAt = if (isSelfRegistered) {
            now.plus(REGISTRATION_VALIDITY)
        } else {
            null
        },
        registrationOtpAttempts = 0,
        twoFactorEnabled = false
    )
}

fun User.toUpdatedUser(input: UpdateUserApi): User {
    if (input.biography != null && input.biography.length > MAX_BIOGRAPHY_LENGTH) {
        throw UserInvalidBiographyException(
            "Biography must not exceed $MAX_BIOGRAPHY_LENGTH characters"
        )
    }

    return this.copy(
        firstName = input.firstName ?: this.firstName,
        lastName = input.lastName ?: this.lastName,
        phoneNumber = input.phoneNumber ?: this.phoneNumber,
        eventCategoryPreferences =
            input.eventCategoryPreferences?.map { it.toEventCategory() } ?: this.eventCategoryPreferences,
        companyName = input.companyName ?: this.companyName,
        language = input.language ?: this.language,
        notificationsEnabled = input.notificationsEnabled ?: this.notificationsEnabled,
        biography = input.biography ?: this.biography,
        twoFactorEnabled = input.twoFactorEnabled ?: this.twoFactorEnabled,
        updatedAt = Instant.now(),
    )
}

fun User.toSuspendedUser(suspendUntil: Instant): User {
    return this.copy(
        status = UserStatus.SUSPENDED,
        suspendedUntil = suspendUntil,
        updatedAt = Instant.now(),
    )
}

fun UserRoleApi.toUserRole(): UserRole {
    return when (this) {
        UserRoleApi.ADMIN -> UserRole.ADMIN
        UserRoleApi.VOLUNTEER -> UserRole.VOLUNTEER
        UserRoleApi.NGO -> UserRole.NGO
        UserRoleApi.MODERATOR -> UserRole.MODERATOR
    }
}

fun CreateUserRoleApi.toUserRole(): UserRole {
    return when (this) {
        CreateUserRoleApi.MODERATOR -> UserRole.MODERATOR
        CreateUserRoleApi.NGO -> UserRole.NGO
        CreateUserRoleApi.VOLUNTEER -> UserRole.VOLUNTEER
    }
}

fun UserStatusApi.toUserStatus(): UserStatus {
    return when (this) {
        UserStatusApi.ACTIVE -> UserStatus.ACTIVE
        UserStatusApi.INACTIVE -> UserStatus.INACTIVE
        UserStatusApi.SUSPENDED -> UserStatus.SUSPENDED
    }
}

// *** GQL extensions ***
fun UserApi.toUserGql(): UserGQL {
    return UserGQL(
        id,
        firstName,
        lastName,
        email,
        phoneNumber,
        role.toUserRoleGql(),
        status.toUserStatusGql(),
        createdAt.toString(),
        updatedAt?.toString(),
        eventCategoryPreferences?.map { it.toEventCategoryGql() },
        suspendedUntil?.toString(),
        language.toLanguageGql(),
        notificationsEnabled,
        forceResetPassword,
        twoFactorEnabled,
        biography
    )
}

fun UserStatusApi.toUserStatusGql(): UserStatusGQL {
    return when (this) {
        UserStatusApi.ACTIVE -> UserStatusGQL.ACTIVE
        UserStatusApi.INACTIVE -> UserStatusGQL.INACTIVE
        UserStatusApi.SUSPENDED -> UserStatusGQL.SUSPENDED
    }
}

fun UserRoleApi.toUserRoleGql(): UserRoleGQL {
    return when (this) {
        UserRoleApi.ADMIN -> UserRoleGQL.ADMIN
        UserRoleApi.VOLUNTEER -> UserRoleGQL.VOLUNTEER
        UserRoleApi.NGO -> UserRoleGQL.NGO
        UserRoleApi.MODERATOR -> UserRoleGQL.MODERATOR
    }
}

fun User.toPasswordResetRequestedUser(temporaryPasswordHash: String): User {
    return this.copy(
        passwordHash = temporaryPasswordHash,
        forceResetPassword = true,
        updatedAt = Instant.now()
    )
}

fun User.toPasswordUpdatedUser(newPasswordHash: String): User {
    return this.copy(
        passwordHash = newPasswordHash,
        forceResetPassword = false,
        updatedAt = Instant.now()
    )
}

fun EventCategoryApi.toEventCategoryGql(): EventCategoryGQL {
    return when (this) {
        EventCategoryApi.SOCIAL -> EventCategoryGQL.SOCIAL
        EventCategoryApi.ANIMAL_CARE -> EventCategoryGQL.ANIMAL_CARE
        EventCategoryApi.CHILDREN_AND_YOUTH -> EventCategoryGQL.CHILDREN_AND_YOUTH
        EventCategoryApi.EDUCATION -> EventCategoryGQL.EDUCATION
        EventCategoryApi.ENVIRONMENT -> EventCategoryGQL.ENVIRONMENT
        EventCategoryApi.HEALTH -> EventCategoryGQL.HEALTH
        EventCategoryApi.DISABILITY_SUPPORT -> EventCategoryGQL.DISABILITY_SUPPORT
        EventCategoryApi.ELDERLY_CARE -> EventCategoryGQL.ELDERLY_CARE
        EventCategoryApi.DISASTER -> EventCategoryGQL.DISASTER
        EventCategoryApi.POVERTY -> EventCategoryGQL.POVERTY
        EventCategoryApi.CULTURE -> EventCategoryGQL.CULTURE
        EventCategoryApi.SPORT -> EventCategoryGQL.SPORT
        EventCategoryApi.FESTIVALS -> EventCategoryGQL.FESTIVALS
        EventCategoryApi.TECHNOLOGY -> EventCategoryGQL.TECHNOLOGY
        EventCategoryApi.BUSINESS -> EventCategoryGQL.BUSINESS
        EventCategoryApi.EMPLOYMENT -> EventCategoryGQL.EMPLOYMENT
        EventCategoryApi.SCIENCE -> EventCategoryGQL.SCIENCE
        EventCategoryApi.AGRICULTURE -> EventCategoryGQL.AGRICULTURE
        EventCategoryApi.CONSTRUCTION -> EventCategoryGQL.CONSTRUCTION
        EventCategoryApi.RELIGION -> EventCategoryGQL.RELIGION
        EventCategoryApi.HUMAN_RIGHTS -> EventCategoryGQL.HUMAN_RIGHTS
        EventCategoryApi.LEGAL -> EventCategoryGQL.LEGAL
        EventCategoryApi.SAFETY -> EventCategoryGQL.SAFETY
        EventCategoryApi.FAMILY -> EventCategoryGQL.FAMILY
        EventCategoryApi.LGBTQ_PLUS -> EventCategoryGQL.LGBTQ_PLUS
        EventCategoryApi.REFUGEE_SUPPORT -> EventCategoryGQL.REFUGEE_SUPPORT
        EventCategoryApi.INTERNATIONAL_VOLUNTEERING -> EventCategoryGQL.INTERNATIONAL_VOLUNTEERING
        EventCategoryApi.TOURISM -> EventCategoryGQL.TOURISM
        EventCategoryApi.HERITAGE -> EventCategoryGQL.HERITAGE
        EventCategoryApi.MEDIA -> EventCategoryGQL.MEDIA
        EventCategoryApi.GARDENING -> EventCategoryGQL.GARDENING
        EventCategoryApi.PEACE -> EventCategoryGQL.PEACE
        EventCategoryApi.ADDICTION_RECOVERY -> EventCategoryGQL.ADDICTION_RECOVERY
        EventCategoryApi.OTHER -> EventCategoryGQL.OTHER
    }
}

fun LanguageApi.toLanguageGql(): LanguageGQL {
    return when (this) {
        LanguageApi.RO -> LanguageGQL.RO
        LanguageApi.EN -> LanguageGQL.EN
        LanguageApi.RU -> LanguageGQL.RU
    }
}


