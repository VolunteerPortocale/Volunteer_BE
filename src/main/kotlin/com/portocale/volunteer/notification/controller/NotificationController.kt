package com.portocale.volunteer.notification.controller

import com.portocale.volunteer.notification.service.EmailService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Locale

@RestController
@Tag(name = "Notifications", description = "Endpoints for notifications and emails")
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val emailService: EmailService
) {

    @PostMapping("/email")
    @Operation(
        summary = "Send a test email using Brevo SMTP and audit to MongoDB",
        description = "Renders a Velocity template with i18n support, sends via Brevo SMTP, and audits to MongoDB."
    )
    fun sendTestEmail(
        @Parameter(description = "Recipient email address", example = "your.email@example.com", required = true)
        @RequestParam to: String,
        @Parameter(description = "Template name (inside templates/)", example = "welcome.vm")
        @RequestParam(defaultValue = "welcome.vm") templateName: String,
        @Parameter(description = "Language code: en, ro, ru", example = "en")
        @RequestParam(defaultValue = "en") lang: String
    ): ResponseEntity<Map<String, String>> {
        val locale = Locale.forLanguageTag(lang)
        emailService.sendTemplatedEmail(
            to = to,
            subject = "Welcome to Volunteerio - Live Test",
            templateName = templateName,
            locale = locale
        )

        return ResponseEntity.ok(
            mapOf(
                "status" to "SENT",
                "recipient" to to,
                "template" to templateName,
                "message" to "Email dispatched and audited to MongoDB."
            )
        )
    }
}

