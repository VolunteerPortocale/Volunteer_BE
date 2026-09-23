package com.portocale.volunteer.enrollment.repository

import com.portocale.volunteer.enrollment.Enrollment
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EnrollmentRepository : MongoRepository<Enrollment, String> {
    fun findByEventIdAndUserId(eventId: String, userId: String): Enrollment?
}
