package com.example.exception

import org.springframework.http.HttpStatus

class ConflictException : BusinessException {

    constructor(message: String) : super(
        message = message,
        status = HttpStatus.CONFLICT,
        errorCode = "CONFLICT"
    )

    constructor(resourceType: String, field: String, value: String) : super(
        message = "$resourceType with $field '$value' already exists",
        status = HttpStatus.CONFLICT,
        errorCode = "DUPLICATE_RESOURCE"
    )
}
