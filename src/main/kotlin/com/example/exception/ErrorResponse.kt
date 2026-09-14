package com.example.exception

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

/**
 * Standardized JSON structure sent back to API clients whenever an error occurs.
 *
 * @JsonInclude(NON_NULL) ensures that fields with null values (like validationErrors
 * on a 404 error) are excluded from the JSON response instead of showing as `"validationErrors": null`.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    // UTC timestamp of when the error occurred
    val timestamp: Instant? = null,

    // HTTP status code (e.g. 404, 400, 500)
    val status: Int,

    // HTTP status name (e.g. "Not Found", "Bad Request", "Internal Server Error")
    val error: String? = null,

    // Human-readable message explaining what went wrong
    val message: String? = null,

    // The API endpoint URL that was called (e.g. "/api/v1/users/42")
    val path: String? = null,

    // Optional trace identifier for tracing this error in distributed log systems
    val traceId: String? = null,

    // Detailed field-level errors for validation failures (e.g. "email" -> ["Invalid format"])
    val validationErrors: Map<String, List<String>>? = null,

    // Optional additional debug details
    val details: Any? = null
)
