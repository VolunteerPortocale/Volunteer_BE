package com.portocale.volunteer.notification.service

import java.security.SecureRandom
import org.springframework.stereotype.Component

private const val BOUND = 1_000_000

@Component
class OtpGenerator {

    private val secureRandom = SecureRandom()

    fun generate(): String {
        return "%06d".format(
            secureRandom.nextInt(BOUND)
        )
    }
}
