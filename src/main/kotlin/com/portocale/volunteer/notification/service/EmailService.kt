package com.portocale.volunteer.notification.service

import java.util.Locale

interface EmailService {

    fun sendTemplatedEmail(
        to: String,
        templateName: String,
        templateModel: Map<String, Any> = emptyMap(),
        locale: Locale = Locale.ENGLISH,
        subject: String? = null,
        inlineImages: Map<String, ByteArray> = emptyMap()
    )
}
