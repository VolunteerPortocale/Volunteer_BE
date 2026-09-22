package com.portocale.volunteer.users

import com.portocale.volunteer.event.EventCategory
import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class User(
    @Id
    val id: String? = null,
    val firstName: String,
    val lastName: String,
    @Indexed(unique = true)
    val email: String,
    val passwordHash: String,
    val phoneNumber: String,
    val role: UserRole,
    val status: UserStatus,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val eventCategoryPreferences: List<EventCategory>? = null,
    val companyName: String? = null,
    val suspendedUntil: Instant? = null,

    val registrationOtpHash: String? = null,
    @Indexed(expireAfter = "0s")
    val registrationExpiresAt: Instant? = null,
    val registrationOtpAttempts: Int = 0
)

enum class UserRole {
    ADMIN,
    MODERATOR,
    NGO,
    VOLUNTEER
}

enum class UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
}
