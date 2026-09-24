package com.portocale.volunteer.notification.service

import com.portocale.volunteer.config.LanguageApi

interface EmailService {
    fun sendEnrollmentConfirmation(
        eventId: String,
        userId: String,
        email: String,
        language: LanguageApi
    )

    fun sendRegistrationEmail(
        to: String,
        firstName: String,
        otp: String,
        language: LanguageApi
    )
}

