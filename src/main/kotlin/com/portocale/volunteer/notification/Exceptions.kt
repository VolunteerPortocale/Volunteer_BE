package com.portocale.volunteer.notification

import com.portocale.volunteer.BusinessException
import com.portocale.volunteer.ErrorCode
import org.springframework.http.HttpStatus

class EmailNotFoundException
    (
    message: String = "Email address not found",
    status: HttpStatus = HttpStatus.NOT_FOUND
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E404_004.value
)

class EmailPlaceholderException
    (
    message: String = "Replace me",
    status: HttpStatus = HttpStatus.NOT_FOUND
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E404_004.value
)
