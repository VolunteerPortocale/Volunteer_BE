package com.example.exception

import org.springframework.http.HttpStatus

class ValidationException(
    val errors: Map<String, List<String>>
) : BusinessException(
    message = "Validation failed",
    status = HttpStatus.BAD_REQUEST,
    errorCode = "VALIDATION_ERROR"
)
