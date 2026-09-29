package com.portocale.volunteer.enrollment

import com.portocale.volunteer.BusinessException
import com.portocale.volunteer.ErrorCode
import org.springframework.http.HttpStatus

class EnrollmentNotFoundException
    (
    message: String = "Enrollment not found",
    status: HttpStatus = HttpStatus.NOT_FOUND
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E404_005.value
)

class EnrollmentConflictException
    (
    message: String = "Enrollment conflict",
    status: HttpStatus = HttpStatus.CONFLICT
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E409_004.value
)
