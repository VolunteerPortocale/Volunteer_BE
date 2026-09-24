package com.portocale.volunteer.enrollment

import jakarta.validation.constraints.NotBlank

data class CreateEnrollmentApi(
    @field:NotBlank(message = "event ID required")
    val eventId: String
)

data class EnrollmentResponseApi(
    val id: String,
    val eventId: String,
    val userId: String,
    val status: String
)