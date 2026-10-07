package com.portocale.volunteer.twoFactor.service

import com.portocale.volunteer.config.jwt.Principal
import com.portocale.volunteer.notification.service.EmailService
import com.portocale.volunteer.notification.service.OtpGenerator
import com.portocale.volunteer.twoFactor.ConfirmTwoFactorApi
import com.portocale.volunteer.twoFactor.DisableTwoFactorApi
import com.portocale.volunteer.twoFactor.TwoFactor
import com.portocale.volunteer.twoFactor.TwoFactorAlreadyEnabledException
import com.portocale.volunteer.twoFactor.TwoFactorInvalidOtpStateException
import com.portocale.volunteer.twoFactor.TwoFactorNotEnabledException
import com.portocale.volunteer.twoFactor.repository.TwoFactorRepository
import com.portocale.volunteer.users.UserApi
import com.portocale.volunteer.users.UserInvalidCredentialsException
import com.portocale.volunteer.users.service.UserService
import java.time.Duration
import java.time.Instant
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

    override fun initiateTwoFactorActivation(userId: String): Boolean {
        return initTwoFactory(userId)
    }

    override fun selfInitiateTwoFactorActivation(): Boolean {
        val user = userService.getById(currentUserId())
        return initTwoFactory(user.id)
    }

    override fun confirmTwoFactorActivation(userId: String, input: ConfirmTwoFactorApi): Boolean {
        return validateTwoFactory(userId, input)
    }

    override fun selfConfirmTwoFactorActivation(input: ConfirmTwoFactorApi): Boolean {
        val userId = currentUserId()
        return validateTwoFactory(userId, input)
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


    private fun handleInvalidTwoFactorOtp(twoFactor: TwoFactor): Nothing {
        twoFactorRepository.save(twoFactor.incrementAttempts())
        throw TwoFactorInvalidOtpStateException("Invalid OTP code")
    }

    private fun currentUserId(): String =
        (SecurityContextHolder.getContext().authentication as Principal).userId

    private fun initTwoFactory(userId: String): Boolean {
        val user = userService.getById(userId)
        dispatchTwoFactorOtp(user)
        return true
    }

    private fun validateTwoFactory(
        userId: String,
        input: ConfirmTwoFactorApi
    ): Boolean {
        val twoFactor = this.getByUserId(userId)

        val isStructureValid = twoFactor.isValid()
        val isCodeValid = twoFactor.isValidateOtpCode(input.otp, passwordEncoder)

        if (isCodeValid.not()) handleInvalidTwoFactorOtp(twoFactor)

        if (isStructureValid && isCodeValid) twoFactorRepository.save(twoFactor.clearOtp())

        return isStructureValid && isCodeValid
    }

    private fun getByUserId(userId: String): TwoFactor {
        return twoFactorRepository.findByUserId(userId)
            .orElseThrow { TwoFactorInvalidOtpStateException() }
    }
}
