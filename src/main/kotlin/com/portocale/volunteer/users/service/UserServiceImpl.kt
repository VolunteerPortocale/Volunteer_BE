package com.portocale.volunteer.users.service

import com.portocale.volunteer.notification.service.EmailService
import com.portocale.volunteer.notification.service.OtpGenerator
import com.portocale.volunteer.users.CreateUserApi
import com.portocale.volunteer.users.LoginUserApi
import com.portocale.volunteer.users.TooManyOtpAttemptsException
import com.portocale.volunteer.users.UpdateUserApi
import com.portocale.volunteer.users.User
import com.portocale.volunteer.users.UserApi
import com.portocale.volunteer.users.UserConflictException
import com.portocale.volunteer.users.UserInvalidCredentialsException
import com.portocale.volunteer.users.UserInvalidOtpStateException
import com.portocale.volunteer.users.UserNotFoundException
import com.portocale.volunteer.users.UserStatus
import com.portocale.volunteer.users.repository.UserRepository
import com.portocale.volunteer.users.toSuspendedUser
import com.portocale.volunteer.users.toUpdatedUser
import com.portocale.volunteer.users.toUser
import com.portocale.volunteer.users.toUserApi
import java.time.Duration
import java.time.Instant
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@SuppressWarnings("TooManyFunctions")
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val otpGenerator: OtpGenerator,
    private val emailService: EmailService
) : UserService {

    companion object {
        private val REGISTRATION_VALIDITY = Duration.ofDays(1)
        private const val MAX_OTP_ATTEMPTS = 5
    }

    override fun getAll(): List<UserApi> {
        return userRepository.findAll()
            .map { it.toUserApi() }
    }

    override fun getById(id: String): UserApi {
        return getUserById(id).toUserApi()
    }

    override fun create(input: CreateUserApi): UserApi {
        val email = input.email.trim().lowercase()
        throwingExistsByEmail(email)
        return userRepository.save(input.toUser(passwordEncoder)).toUserApi()
    }

    override fun selfRegister(
        input: CreateUserApi
    ): UserApi {
        val email = input.email.trim().lowercase()

        throwingExistsByEmail(email)

        val otp = otpGenerator.generate()

        val user = input.toUser(passwordEncoder, true, otp)

        val savedUser = userRepository.save(user)

        emailService.sendRegistrationEmail(
            to = savedUser.email,
            firstName = savedUser.firstName,
            otp = otp
        )

        return savedUser.toUserApi()
    }

    override fun validateRegistrationOtp(
        email: String,
        otp: String
    ): UserApi {
        val user = getInactiveUserByEmail(email)

        validateRegistrationExpiration(user)
        validateOtpAttempts(user)

        val otpHash = getRegistrationOtpHash(user)

        if (!passwordEncoder.matches(otp, otpHash)) {
            handleInvalidOtp(user)
        }

        return activateUser(user)
    }

    override fun resendRegistrationOtp(
        email: String
    ) {

        val normalizedEmail = email.trim().lowercase()

        val user = getInactiveUserByEmail(normalizedEmail)

        val otp = otpGenerator.generate()

        val updatedUser = user.copy(
            registrationOtpHash =
                passwordEncoder.encode(otp),
            registrationExpiresAt =
                Instant.now().plus(REGISTRATION_VALIDITY),
            registrationOtpAttempts = 0,
            updatedAt = Instant.now()
        )

        userRepository.save(updatedUser)

        emailService.sendRegistrationEmail(
            to = updatedUser.email,
            firstName = updatedUser.firstName,
            otp = otp
        )
    }

    override fun update(
        id: String,
        input: UpdateUserApi,
    ): UserApi {
        val user = getUserById(id)
        val updatedUser = user.toUpdatedUser(input)

        return userRepository.save(updatedUser).toUserApi()
    }

    override fun suspend(
        id: String,
        suspendedUntil: Instant,
    ): UserApi {
        val user = getUserById(id)

        val suspendedUser = user.toSuspendedUser(suspendedUntil)

        return userRepository.save(suspendedUser).toUserApi()
    }

    override fun delete(id: String): Boolean {
        if (!userRepository.existsById(id)) {
            return false
        }

        userRepository.deleteById(id)
        return true
    }

    override fun login(input: LoginUserApi): UserApi {
        val user = getUserByEmail(input.email)
        if (!passwordEncoder.matches(input.password, user.passwordHash)) {
            throw UserInvalidCredentialsException()
        }

        return user.toUserApi()
    }

    private fun getUserById(id: String): User {
        return userRepository.findById(id)
            .orElseThrow {
                UserNotFoundException(id)
            }
    }

    private fun getUserByEmail(email: String): User {
        val email = email.trim().lowercase()

        return userRepository.findByEmail(email)
            .orElseThrow {
                UserNotFoundException(email)
            }
    }

    private fun getInactiveUserByEmail(email: String): User {
        val email = email.trim().lowercase()

        return userRepository.findByEmailAndStatus(email)
            .orElseThrow {
                UserNotFoundException(email)
            }
    }

    private fun throwingExistsByEmail(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw UserConflictException(
                "User with email $email already exists"
            )
        }
    }

    private fun validateRegistrationExpiration(user: User) {
        val expiresAt = user.registrationExpiresAt
            ?: throw UserInvalidOtpStateException()

        if (Instant.now().isAfter(expiresAt)) {
            userRepository.delete(user)
            throw UserInvalidOtpStateException()
        }
    }

    private fun validateOtpAttempts(user: User) {
        if (user.registrationOtpAttempts >= MAX_OTP_ATTEMPTS) {
            throw TooManyOtpAttemptsException()
        }
    }

    private fun getRegistrationOtpHash(user: User): String {
        return user.registrationOtpHash
            ?: throw UserInvalidOtpStateException()
    }

    private fun handleInvalidOtp(user: User): Nothing {
        userRepository.save(
            user.copy(
                registrationOtpAttempts =
                    user.registrationOtpAttempts + 1
            )
        )

        throw UserInvalidOtpStateException()
    }

    private fun activateUser(user: User): UserApi {
        val activatedUser = user.copy(
            status = UserStatus.ACTIVE,
            registrationOtpHash = null,
            registrationExpiresAt = null,
            registrationOtpAttempts = 0,
            updatedAt = Instant.now()
        )

        return userRepository.save(activatedUser)
            .toUserApi()
    }
}
