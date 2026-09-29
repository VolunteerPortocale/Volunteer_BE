package com.portocale.volunteer.enrollment.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.config.jwt.Principal
import com.portocale.volunteer.enrollment.CreateEnrollmentApi
import com.portocale.volunteer.enrollment.Enrollment
import com.portocale.volunteer.enrollment.EnrollmentConflictException
import com.portocale.volunteer.enrollment.EnrollmentNotFoundException
import com.portocale.volunteer.enrollment.EnrollmentResponseApi
import com.portocale.volunteer.enrollment.EnrollmentStatus
import com.portocale.volunteer.enrollment.GenericStatusApi
import com.portocale.volunteer.enrollment.repository.EnrollmentRepository
import com.portocale.volunteer.enrollment.toEnrollmentResponseApi
import com.portocale.volunteer.enrollment.toEntity
import com.portocale.volunteer.event.service.EventService
import com.portocale.volunteer.notification.service.EmailService
import com.portocale.volunteer.users.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class EnrollmentServiceImpl(
    private val enrollmentRepository: EnrollmentRepository,
    private val eventService: EventService,
    private val userService: UserService,
    private val emailService: EmailService
) : EnrollmentService {

    override fun enroll(request: CreateEnrollmentApi, language: LanguageApi): EnrollmentResponseApi {
        val event = eventService.getById(request.eventId, language)

        val authenticatedUser = SecurityContextHolder.getContext().authentication as Principal

        val user = userService.getById(authenticatedUser.userId)

        if (enrollmentRepository.existsByEventIdAndUserId(event.id, user.id)) error(EnrollmentConflictException())

        val enrollment = enrollmentRepository.save(request.toEntity(user.id)).toEnrollmentResponseApi()

        emailService.sendEnrollmentConfirmation(
            enrollmentId = enrollment.id,
            email = user.email,
            language = language
        )

        return enrollment
    }

    override fun confirmEnrollment(enrollmentId: String): GenericStatusApi {
        val enrollment = throwingGetById(enrollmentId)
        enrollmentRepository.save(enrollment.copy(status = EnrollmentStatus.CONFIRMED))
        return GenericStatusApi.OK
    }

    private fun throwingGetById(enrollmentId: String): Enrollment {
        return enrollmentRepository.findById(enrollmentId)
            .orElseThrow { EnrollmentNotFoundException() }
    }
}
