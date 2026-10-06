package com.portocale.volunteer.twoFactor

import com.portocale.volunteer.BusinessException
import com.portocale.volunteer.ErrorCode
import org.springframework.http.HttpStatus

class TwoFactorAlreadyEnabledException(
    message: String = "Two-factor authentication is already enabled",
    status: HttpStatus = HttpStatus.BAD_REQUEST
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E400_004.value
)

class TwoFactorNotEnabledException(
    message: String = "Two-factor authentication is not enabled",
    status: HttpStatus = HttpStatus.BAD_REQUEST
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E400_005.value
)

class TwoFactorInvalidOtpStateException(
    message: String = "Invalid OTP code",
    status: HttpStatus = HttpStatus.BAD_REQUEST
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E400_002.value
)
