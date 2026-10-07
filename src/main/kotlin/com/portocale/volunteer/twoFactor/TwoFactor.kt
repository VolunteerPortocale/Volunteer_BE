package com.portocale.volunteer.twoFactor

import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.crypto.password.PasswordEncoder

const val MAX_OTP_ATTEMPTS = 3

@Document("two_factor")
data class TwoFactor(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val userId: String,
    val otpHash: String,
    @Indexed(expireAfter = "0s")
    val expiresAt: Instant,
    val attempts: Int = 0,
    val updatedAt: Instant = Instant.now()
) {
    fun isValid(): Boolean {
        return this.isExpired().not() && this.hasExceededAttempts().not() && this.otpHash.isNotEmpty()
    }

    fun isOtpCodeValid(rawOtp: String, encoder: PasswordEncoder): Boolean {
        return encoder.matches(rawOtp, this.otpHash)
    }

    fun incrementAttempts(): TwoFactor = copy(
        attempts = attempts + 1,
        updatedAt = Instant.now()
    )

    private fun isExpired(): Boolean {
        val exp = expiresAt ?: return true
        return Instant.now().isAfter(exp)
    }

    fun hasExceededAttempts(): Boolean = attempts >= MAX_OTP_ATTEMPTS
}


