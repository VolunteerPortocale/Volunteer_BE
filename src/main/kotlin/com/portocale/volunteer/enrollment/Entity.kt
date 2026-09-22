package com.portocale.volunteer.enrollment

import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Enrollment(
    @Id
    val id: String?,
    val eventId: String,
    val userId: String,
    val enrolledAt: Instant,
    val status: EnrollmentStatus,
    val statusHistory: List<EnrollmentStatusHistory>,
)

enum class EnrollmentStatus {
    PENDING,
    CONFIRMED,
    COMPLETED
}

data class EnrollmentStatusHistory(
    val status: EnrollmentStatus,
    val occurredAt: Instant,
    val actor: String
)
