package com.portocale.volunteer.notification.controller

import com.portocale.volunteer.notification.service.EmailService
import com.portocale.volunteer.qr.service.QrService
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
    private val emailService: EmailService,
    private val qrService: QrService
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
        @Parameter(description = "Language code: en, ro, ru, he", example = "en")
        @RequestParam(defaultValue = "en") lang: String
    ): ResponseEntity<Map<String, String>> {
        val locale = Locale.forLanguageTag(lang)
        emailService.sendTemplatedEmail(
            to = to,
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

    @PostMapping("/email-confirmation")
    @Operation(
        summary = "Send event enrollment confirmation email with presence QR code",
        description = "Generates a presence confirmation QR code and emails it inline to the volunteer."
    )
    fun sendEnrollmentConfirmation(
        @Parameter(description = "Recipient email address", example = "volunteer@example.com", required = true)
        @RequestParam to: String,
        @Parameter(description = "Event ID", example = "1234qwerty", required = true)
        @RequestParam eventId: String,
        @Parameter(description = "User ID", example = "1234qwerty", required = true)
        @RequestParam userId: String,
        @Parameter(description = "Language code: en, ro, ru, he", example = "en")
        @RequestParam(defaultValue = "en") lang: String
    ): ResponseEntity<Map<String, String>> {
        val locale = Locale.forLanguageTag(lang)
        val qrBytes = qrService.generateVolunteerPresenceConfirmationQr(eventId, userId)

        emailService.sendTemplatedEmail(
            to = to,
            templateName = "enrolementConfirmation.vm",
            locale = locale,
            inlineImages = mapOf("qrCode" to qrBytes)
        )

        return ResponseEntity.ok(
            mapOf(
                "status" to "SENT",
                "recipient" to to,
                "eventId" to eventId,
                "userId" to userId,
                "template" to "enrolementConfirmation.vm",
                "message" to "Enrollment confirmation email with QR code dispatched successfully."
            )
        )
    }
}

