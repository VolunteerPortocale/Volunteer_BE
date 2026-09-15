package com.example.demo.users

import com.example.exception.BusinessException
import org.springframework.http.HttpStatus

class UserNotFoundException : BusinessException {

    constructor(message: String) : super(
        message = message,
        status = HttpStatus.NOT_FOUND,
        errorCode = "NOT_FOUND"
    )

    constructor(resourceType: String, field: String, value: String) : super(
        message = "$resourceType with $field '$value' not found",
        status = HttpStatus.NOT_FOUND,
        errorCode = "RESOURCE_NOT_FOUND"
    )

    constructor(id: Any) : super(
        message = "User not found with id: $id",
        status = HttpStatus.NOT_FOUND,
        errorCode = "USER_NOT_FOUND"
    )
}

class UserConflictException : BusinessException {

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