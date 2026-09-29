package com.portocale.volunteer.enrollment

import com.portocale.volunteer.event.CreateEventApi
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(name = "CreateEnrollment")
data class CreateEnrollmentApi(
    @field:NotBlank(message = "event ID required")
    val eventId: String
)

@Schema(name = "EnrollmentResponse")
data class EnrollmentResponseApi(
    val id: String,
    val eventId: String,
    val userId: String,
    val status: String
)

@Schema(name = "GenericStatus", enumAsRef = true)
enum class GenericStatusApi {
    OK,
    NOK
}