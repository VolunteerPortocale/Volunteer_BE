package com.portocale.volunteer.event

import com.portocale.volunteer.BusinessException
import com.portocale.volunteer.ErrorCode
import org.springframework.http.HttpStatus

class EventNotFoundException
    (
    message: String = "Event not found",
    status: HttpStatus = HttpStatus.NOT_FOUND
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E404_002.value
)

class EventConflictException
    (
    message: String = "Event conflict",
    status: HttpStatus = HttpStatus.CONFLICT
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E409_003.value
)
