package com.example.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.Instant

/**
 * Global centralized exception handler for all controllers in the application.
 *
 * What does @RestControllerAdvice do?
 * - Tells Spring Boot to intercept any uncaught exceptions thrown from any @RestController.
 * - Automatically serializes returned objects (like ErrorResponse) into JSON responses.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    // Logger instance to record error details on the server console / log files
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * 1. Handles all custom business exceptions.
     *
     * Because [UserNotFoundException], [ConflictException], [ResourceNotFoundException],
     * and [ValidationException] all inherit from [BusinessException], this single method
     * handles all of them!
     *
     * It uses the HTTP status (e.g., 404, 409, 400) defined in each exception.
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        ex: BusinessException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        // Log a warning on the server console (includes the error code and message)
        log.warn("Business exception [{}]: {}", ex.errorCode, ex.message)

        // If the exception happens to be a ValidationException, extract its field errors map
        val validationErrors = (ex as? ValidationException)?.errors

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = ex.status.value(),                                       // e.g. 404 or 409
            error = ex.status.reasonPhrase,                                   // e.g. "Not Found"
            message = ex.message,                                             // e.g. "User not found with id: 42"
            path = request.getDescription(false).replace("uri=", ""),         // Request URL that caused this
            validationErrors = validationErrors
        )

        return ResponseEntity(error, ex.status)
    }

    /**
     * 2. Handles standard Spring validation failures.
     *
     * Thrown automatically by Spring when request bodies annotated with @Valid
     * fail constraints (such as @NotBlank, @Email, @Size, etc.).
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        // Collect and group validation errors by field name (e.g. "email" -> ["must not be blank", "must be a valid email"])
        val errors = ex.bindingResult.fieldErrors
            .groupBy({ it.field }, { it.defaultMessage ?: "Invalid value" })

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),                          // HTTP 400
            error = HttpStatus.BAD_REQUEST.reasonPhrase,                      // "Bad Request"
            message = "Validation failed",
            path = request.getDescription(false).replace("uri=", ""),
            validationErrors = errors
        )

        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    /**
     * 3. Fallback catch-all handler for unexpected errors.
     *
     * Catches everything else that was not anticipated (e.g. NullPointerException,
     * database connection failures, external API timeouts).
     *
     * Logs the full stack trace for developers, but returns a generic message to the client
     * to avoid leaking sensitive internal details.
     */
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        // Log the full stack trace at ERROR level for debugging
        log.error("Unhandled exception: {}", ex.message, ex)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),                // HTTP 500
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,            // "Internal Server Error"
            message = "An unexpected error occurred",                         // Generic client-safe message
            path = request.getDescription(false).replace("uri=", "")
        )

        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
