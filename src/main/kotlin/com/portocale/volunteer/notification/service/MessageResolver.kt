package com.portocale.volunteer.notification.service

import org.springframework.context.MessageSource
import java.util.*

class MessageResolver(
    private val messageSource: MessageSource
) {
    fun get(key: String, locale: Locale): String =
        messageSource.getMessage(key, null, locale)
}
