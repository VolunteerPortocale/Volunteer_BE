package com.portocale.volunteer.enrollment.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.config.jwt.Principal
import com.portocale.volunteer.enrollment.CreateEnrollmentApi
import com.portocale.volunteer.enrollment.Enrollment
import com.portocale.volunteer.enrollment.EnrollmentResponseApi
import com.portocale.volunteer.enrollment.EnrollmentStatus
import com.portocale.volunteer.enrollment.repository.EnrollmentRepository
import com.portocale.volunteer.event.EventNotFoundException
import com.portocale.volunteer.event.repository.EventRepository
import com.portocale.volunteer.notification.service.EmailService
import com.portocale.volunteer.users.UserNotFoundException
import com.portocale.volunteer.users.repository.UserRepository
import java.time.Instant
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class EnrollmentServiceImpl(
    private val enrollmentRepository: EnrollmentRepository,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val emailService: EmailService
) : EnrollmentService {

    override fun enroll(request: CreateEnrollmentApi, language: LanguageApi): EnrollmentResponseApi {
        if (!eventRepository.existsById(request.eventId)) {
            throw EventNotFoundException("Event not found with ID: ${request.eventId}")
        }

        val authenticatedUser = SecurityContextHolder.getContext().authentication as Principal

        val user = userRepository.findById(authenticatedUser.userId)
            .or { userRepository.findByEmail(authenticatedUser.email) }
            .orElseThrow { UserNotFoundException("User not found: ${authenticatedUser.userId}") }

        val enrollment = enrollmentRepository.save(
            Enrollment(
                id = null,
                eventId = request.eventId,
                userId = user.id!!,
                enrolledAt = Instant.now(),
                status = EnrollmentStatus.CONFIRMED,
                statusHistory = emptyList()
            )
        )

        emailService.sendEnrollmentConfirmation(
            eventId = request.eventId,
            userId = user.id,
            email = user.email,
            language = language
        )

        return EnrollmentResponseApi(
            id = enrollment.id!!,
            eventId = enrollment.eventId,
            userId = enrollment.userId,
            status = enrollment.status.name
        )
    }
}