package com.portocale.volunteer.users

import com.portocale.volunteer.BusinessException
import com.portocale.volunteer.ErrorCode
import org.springframework.http.HttpStatus

class UserNotFoundException
    (
    message: String = "User not found",
    status: HttpStatus = HttpStatus.NOT_FOUND
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E404_001.value
)

class UserConflictException(
    message: String = "User already exists",
    status: HttpStatus = HttpStatus.CONFLICT
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E409_001.value
)

class UserClassCastException(
    message: String = "User class cannot exception",
    status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E500_001.value
)

class UserInvalidCredentialsException(
    message: String = "Invalid credentials",
    status: HttpStatus = HttpStatus.BAD_REQUEST
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E400_001.value
)

class UserInvalidOtpStateException(
    message: String = "Invalid OTP state",
    status: HttpStatus = HttpStatus.BAD_REQUEST
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E400_002.value
)

class TooManyOtpAttemptsException(
    message: String = "Too many attempts",
    status: HttpStatus = HttpStatus.BAD_REQUEST
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E400_003.value
)
