package com.portocale.volunteer.notification.service

import com.portocale.volunteer.config.LanguageApi

interface EmailService {
    fun sendEnrollmentConfirmation(eventId: String, language: LanguageApi)

    fun sendSimpleEmail(to: String, subject: String, content: String, isHtml: Boolean = false)
    fun sendRegistrationEmail(to: String, firstName: String, otp: String)

    fun sendTemplatedEmail(
        to: String,
        subject: String,
        templateName: String,
        templateModel: Map<String, Any> = emptyMap(),
        locale: Locale = Locale.ENGLISH
    )
}
