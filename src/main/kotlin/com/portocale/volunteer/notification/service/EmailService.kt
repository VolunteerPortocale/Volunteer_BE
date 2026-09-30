package com.portocale.volunteer.notification.service

import com.portocale.volunteer.config.LanguageApi

interface EmailService {
    fun sendEnrollmentConfirmation(
        enrollmentId: String,
        email: String,
        language: LanguageApi
    )

    fun sendRegistrationEmail(
        to: String,
        firstName: String,
        otp: String,
        language: LanguageApi
    )

    fun sendEventReminder(
        email: String,
        eventTitle: String,
        eventLocation: String,
        eventStartTime: String,
        enrollmentId: String,
        language: LanguageApi
    )
}

