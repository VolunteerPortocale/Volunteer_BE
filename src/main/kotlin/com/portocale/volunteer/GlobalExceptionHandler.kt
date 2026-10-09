package com.portocale.volunteer

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import java.time.Instant
import org.slf4j.LoggerFactory
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.graphql.execution.ErrorType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

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
     * It uses the HTTP status (e.g., 404, 409, 400) defined in each exception.
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        ex: BusinessException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        // Log a warning on the server console (includes the error code and message)
        log.warn("Business exception [{}]: {}", ex.errorCode, ex.message)

        val error = ErrorResponse(
            timestamp = Instant.now(),
            status = ex.status.value(),                                       // e.g. 404 or 409
            error = ex.status.reasonPhrase,                                   // e.g. "Not Found"
            message = ex.message,                                             // e.g. "User not found with id: 42"
            path = request.getDescription(false).replace("uri=", ""),         // Request URL that caused this
        )

        return ResponseEntity(error, ex.status)
    }

    @GraphQlExceptionHandler
    fun handleGraphQlBusinessException(
        ex: BusinessException,
        errorBuilder: GraphqlErrorBuilder<*>
    ): GraphQLError {
        log.warn("GraphQL business exception [{}]: {}", ex.errorCode, ex.message)

        val errorType = when (ex.status) {
            HttpStatus.BAD_REQUEST,
            HttpStatus.CONFLICT,
            HttpStatus.UNPROCESSABLE_ENTITY -> ErrorType.BAD_REQUEST

            HttpStatus.UNAUTHORIZED -> ErrorType.UNAUTHORIZED
            HttpStatus.FORBIDDEN -> ErrorType.FORBIDDEN
            HttpStatus.NOT_FOUND -> ErrorType.NOT_FOUND

            else -> ErrorType.INTERNAL_ERROR
        }

        return errorBuilder
            .errorType(errorType)
            .message(ex.message)
            .extensions(
                mapOf(
                    "code" to ex.errorCode,
                    "status" to ex.status.value(),
                    "timestamp" to Instant.now().toString()
                )
            )
            .build()
    }
}
