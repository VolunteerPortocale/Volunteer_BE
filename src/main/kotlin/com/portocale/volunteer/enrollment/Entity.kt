package com.portocale.volunteer.enrollment

import java.time.Instant
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Enrollment(
    @Id
    val id: String?,
    val eventId: String,
    val userId: String,
    @Indexed(expireAfter = "365d")
    val enrolledAt: Instant = Instant.now(),
    val status: EnrollmentStatus,
    val statusHistory: List<EnrollmentStatusHistory>? = null
)

enum class EnrollmentStatus {
    ENROLLED,
    CONFIRMED,
    COMPLETED
}

data class EnrollmentStatusHistory(
    val status: EnrollmentStatus,
    val occurredAt: Instant,
    val actor: String
)
