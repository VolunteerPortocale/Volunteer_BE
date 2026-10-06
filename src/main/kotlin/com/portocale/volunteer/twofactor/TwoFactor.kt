package com.portocale.volunteer.twofactor

import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val MAX_OTP_ATTEMPTS = 3

@Document("two_factor")
data class TwoFactor(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val userId: String,
    val enabled: Boolean = false,
    val otpHash: String? = null,
    @Indexed(expireAfter = "0s")
    val expiresAt: Instant? = null,
    val attempts: Int = 0,
    val updatedAt: Instant = Instant.now()
)

fun TwoFactor.clearOtp(enabled: Boolean = this.enabled): TwoFactor = copy(
    enabled = enabled,
    otpHash = null,
    expiresAt = null,
    attempts = 0,
    updatedAt = Instant.now()
)

fun TwoFactor.incrementAttempts(): TwoFactor = copy(
    attempts = attempts + 1,
    updatedAt = Instant.now()
)

fun TwoFactor.isExpired(): Boolean {
    val exp = expiresAt ?: return true
    return Instant.now().isAfter(exp)
}

fun TwoFactor.hasExceededAttempts(): Boolean = attempts >= MAX_OTP_ATTEMPTS
