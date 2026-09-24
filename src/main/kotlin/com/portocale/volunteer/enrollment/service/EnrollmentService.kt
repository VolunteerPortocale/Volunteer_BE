package com.portocale.volunteer.enrollment.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.enrollment.CreateEnrollmentApi
import com.portocale.volunteer.enrollment.EnrollmentResponseApi
import org.springframework.security.access.prepost.PreAuthorize

interface EnrollmentService {
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'NGO', 'VOLUNTEER')")
    fun enroll(request: CreateEnrollmentApi, language: LanguageApi): EnrollmentResponseApi
}

