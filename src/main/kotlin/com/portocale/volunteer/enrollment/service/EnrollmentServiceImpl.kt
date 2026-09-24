package com.portocale.volunteer.enrollment.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.config.jwt.Principal
import com.portocale.volunteer.enrollment.CreateEnrollmentApi
import com.portocale.volunteer.enrollment.EnrollmentResponseApi
import com.portocale.volunteer.enrollment.repository.EnrollmentRepository
import com.portocale.volunteer.enrollment.toEntity
import com.portocale.volunteer.enrollment.toEnrollmentResponseApi
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

        val enrollment = enrollmentRepository.save(request.toEntity(user.id))

        emailService.sendEnrollmentConfirmation(
            eventId = event.id,
            userId = user.id,
            email = user.email,
            language = language
        )

        return enrollment.toEnrollmentResponseApi()
    }
}
