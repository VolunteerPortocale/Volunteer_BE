package com.portocale.volunteer.twofactor.service

import com.portocale.volunteer.config.jwt.Principal
import com.portocale.volunteer.notification.service.EmailService
import com.portocale.volunteer.notification.service.OtpGenerator
import com.portocale.volunteer.twofactor.ConfirmTwoFactorApi
import com.portocale.volunteer.twofactor.DisableTwoFactorApi
import com.portocale.volunteer.twofactor.TwoFactor
import com.portocale.volunteer.twofactor.TwoFactorAlreadyEnabledException
import com.portocale.volunteer.twofactor.TwoFactorInvalidOtpStateException
import com.portocale.volunteer.twofactor.TwoFactorNotEnabledException
import com.portocale.volunteer.twofactor.TwoFactorTooManyOtpAttemptsException
import com.portocale.volunteer.twofactor.VerifyTwoFactorLoginApi
import com.portocale.volunteer.twofactor.clearOtp
import com.portocale.volunteer.twofactor.hasExceededAttempts
import com.portocale.volunteer.twofactor.incrementAttempts
import com.portocale.volunteer.twofactor.isExpired
import com.portocale.volunteer.twofactor.repository.TwoFactorRepository
import com.portocale.volunteer.users.UserApi
import com.portocale.volunteer.users.UserInvalidCredentialsException
import com.portocale.volunteer.users.UserLoginTwoFactorEvent
import com.portocale.volunteer.users.service.UserService
import java.time.Duration
import java.time.Instant
import org.springframework.context.event.EventListener
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class TwoFactorServiceImpl(
    private val twoFactorRepository: TwoFactorRepository,
    private val userService: UserService,
    private val emailService: EmailService,
    private val otpGenerator: OtpGenerator,
    private val passwordEncoder: PasswordEncoder
) : TwoFactorService {

    companion object {
        private val TWO_FACTOR_VALIDITY = Duration.ofMinutes(5)
    }

    override fun initiateTwoFactorActivation(): Boolean {
        val user = userService.getById(currentUserId())
        if (user.twoFactorEnabled) {
            throw TwoFactorAlreadyEnabledException()
        }
        dispatchTwoFactorOtp(user)
        return true
    }

    override fun confirmTwoFactorActivation(input: ConfirmTwoFactorApi): UserApi {
        val userId = currentUserId()
        val twoFactor = getAndValidateTwoFactor(userId, input.otp)
        twoFactorRepository.save(twoFactor.clearOtp(enabled = true))
        return userService.setTwoFactorEnabled(userId, true)
    }

    override fun disableTwoFactor(input: DisableTwoFactorApi): UserApi {
        val userId = currentUserId()
        val user = userService.getById(userId)
        if (!user.twoFactorEnabled) {
            throw TwoFactorNotEnabledException()
        }
        if (!userService.verifyPassword(userId, input.password)) {
            throw UserInvalidCredentialsException("Invalid password")
        }
        twoFactorRepository.findByUserId(userId).ifPresent {
            twoFactorRepository.save(it.clearOtp(enabled = false))
        }
        return userService.setTwoFactorEnabled(userId, false)
    }

    override fun verifyTwoFactorLogin(input: VerifyTwoFactorLoginApi): UserApi {
        val user = userService.getByEmailForAuth(input.email)
        if (!user.twoFactorEnabled) {
            throw TwoFactorNotEnabledException()
        }
        val twoFactor = getAndValidateTwoFactor(user.id, input.otp)
        twoFactorRepository.save(twoFactor.clearOtp())
        return user
    }

    @EventListener
    fun onUserLoginTwoFactor(event: UserLoginTwoFactorEvent) {
        dispatchTwoFactorOtp(event.user)
    }

    private fun getAndValidateTwoFactor(userId: String, rawOtp: String): TwoFactor {
        val twoFactor = twoFactorRepository.findByUserId(userId)
            .orElseThrow { TwoFactorInvalidOtpStateException() }
        validateTwoFactorState(twoFactor)
        val otpHash = twoFactor.otpHash ?: throw TwoFactorInvalidOtpStateException()
        if (!passwordEncoder.matches(rawOtp, otpHash)) {
            handleInvalidTwoFactorOtp(twoFactor)
        }
        return twoFactor
    }

    private fun dispatchTwoFactorOtp(user: UserApi) {
        val otp = otpGenerator.generate()
        val expiresAt = Instant.now().plus(TWO_FACTOR_VALIDITY)
        val encodedOtp = passwordEncoder.encode(otp)
        val existing = twoFactorRepository.findByUserId(user.id).orElseGet {
            TwoFactor(userId = user.id)
        }
        twoFactorRepository.save(
            existing.copy(
                otpHash = encodedOtp,
                expiresAt = expiresAt,
                attempts = 0,
                updatedAt = Instant.now()
            )
        )
        emailService.sendTwoFactorAuth(user.email, user.firstName, otp, user.language)
    }

    private fun validateTwoFactorState(twoFactor: TwoFactor) {
        if (twoFactor.expiresAt == null || twoFactor.isExpired()) {
            twoFactorRepository.save(twoFactor.clearOtp())
            throw TwoFactorInvalidOtpStateException("2FA code has expired")
        }
        if (twoFactor.hasExceededAttempts()) {
            twoFactorRepository.save(twoFactor.clearOtp())
            throw TwoFactorTooManyOtpAttemptsException()
        }
    }

    private fun handleInvalidTwoFactorOtp(twoFactor: TwoFactor): Nothing {
        twoFactorRepository.save(twoFactor.incrementAttempts())
        throw TwoFactorInvalidOtpStateException("Invalid OTP code")
    }

    private fun currentUserId(): String =
        (SecurityContextHolder.getContext().authentication as Principal).userId
}
