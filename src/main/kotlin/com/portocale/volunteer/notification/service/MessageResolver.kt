package com.portocale.volunteer.notification.service

import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class MessageResolver(
    private val messageSource: MessageSource
) {
    fun get(key: String, locale: Locale): String =
        messageSource.getMessage(key, null, locale)
}
