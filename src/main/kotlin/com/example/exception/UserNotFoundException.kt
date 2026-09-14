package com.example.exception

import org.springframework.http.HttpStatus

/**
 * Thrown when a requested user cannot be found in the system (e.g., database lookup fails).
 */
class UserNotFoundException(
    // The identifier of the missing user (e.g., Int id, UUID, String)
    val userId: Any? = null,

    message: String = if (userId != null) "User not found with id: $userId" else "User not found"
) : BusinessException(
    message = message,
    status = HttpStatus.NOT_FOUND,       // Returns HTTP 404
    errorCode = "USER_NOT_FOUND"         // Application-specific machine-readable error code
)
