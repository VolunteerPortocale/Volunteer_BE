package com.portocale.volunteer.enrollment.service

import com.portocale.volunteer.config.LanguageApi
import com.portocale.volunteer.enrollment.CreateEnrollmentApi
import com.portocale.volunteer.enrollment.EnrollmentResponseApi

interface EnrollmentService {
    fun enroll(request: CreateEnrollmentApi, language: LanguageApi): EnrollmentResponseApi
}
