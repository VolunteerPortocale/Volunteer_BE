package com.portocale.volunteer.users

import com.portocale.volunteer.BusinessException
import org.springframework.http.HttpStatus

class UserNotFoundException
    (
    message: String = "User not found",
    status: HttpStatus = HttpStatus.NOT_FOUND
) :    BusinessException(
        message = message,
        status = status,
        errorCode = "404-001"
    )

class UserConflictException(
    message: String = "User allreaady exists",
    status: HttpStatus = HttpStatus.CONFLICT
) : BusinessException (
        message = message,
        status = status,
        errorCode = "409-001"
    )