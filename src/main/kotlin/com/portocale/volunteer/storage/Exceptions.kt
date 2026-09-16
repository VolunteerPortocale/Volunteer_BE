package com.portocale.volunteer.storage

import com.portocale.volunteer.BusinessException
import com.portocale.volunteer.ErrorCode
import org.springframework.http.HttpStatus

class StorageNotFoundException
    (
    message: String = "Folder not found",
    status: HttpStatus = HttpStatus.NOT_FOUND
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E404_003.value
)

class StorageConflictFoundException
    (
    message: String = "Storage duplicated",
    status: HttpStatus = HttpStatus.CONFLICT
) : BusinessException(
    message = message,
    status = status,
    errorCode = ErrorCode.E409_002.value
)
