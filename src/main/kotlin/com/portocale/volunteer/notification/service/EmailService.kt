package com.portocale.volunteer.notification.service

import com.portocale.volunteer.config.LanguageApi

interface EmailService {
    fun sendEnrollmentConfirmation(eventId: String, language: LanguageApi)
}
