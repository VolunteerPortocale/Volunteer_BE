package com.portocale.volunteer

import org.springframework.http.HttpStatus

/**
 * Base open class for all custom domain / business logic exceptions.
 *
 * Why this design?
 * - By having specific exceptions (like UserNotFoundException, ConflictException) inherit from
 *   BusinessException, our [GlobalExceptionHandler] only needs ONE handler method for all of them.
 * - Each subclass can specify its own [status] (HTTP 404, 409, 400, etc.) and [errorCode].
 * - Inherits from [RuntimeException] so it does not require explicit method signatures (unchecked exception).
 */
open class BusinessException(
    override val message: String,
    val status: HttpStatus,          // HTTP response status to return (e.g. HttpStatus.NOT_FOUND)
    val errorCode: String,           // String identifier for the error (e.g. "USER_NOT_FOUND")
    cause: Throwable? = null         // Optional underlying cause if wrapping another exception
) : RuntimeException(message, cause)

enum class ErrorCode(val value: String) {
    E400_001("400-001"),    // InvalidCredentials
    E400_002("400-002"),    // InvalidOtpState
    E400_003("400-003"),    // TooManyOtpAttempts
    E404_001("404-001"),    // UserNotFound
    E404_002("404-002"),    // EventNotFound
    E404_003("404-003"),    // FolderNotFound
    E404_004("404-004"),    // EmailNotFound
    E409_001("409-001"),    // UserConflict
    E409_002("409-002"),    // StorageConflict
    E409_003("409-003"),    // EventConflict
    E500_001("500-001"),    // UserClassCastException
}
