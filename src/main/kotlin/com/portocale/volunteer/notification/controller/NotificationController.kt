package com.portocale.volunteer.notification.controller

import com.portocale.volunteer.config.toLanguageApi
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
    private val emailService: EmailService
) {

    @PostMapping("/email-confirmation")
    @Operation(
        summary = "Send event enrollment confirmation email with presence QR code",
        description = "Generates a presence confirmation QR code and emails it inline to the volunteer."
    )
    fun sendEnrollmentConfirmation(
        @Parameter(description = "Event ID", example = "1234qwerty", required = true)
        @RequestParam eventId: String,
        locale: Locale
    ): ResponseEntity<Map<String, String>> {
        emailService.sendEnrollmentConfirmation(
            eventId,
            locale.toLanguageApi()
        )

        return ResponseEntity.ok(
            mapOf(
                "status" to "SENT",
                "eventId" to eventId,
                "template" to "enrollmentConfirmation.vm",
                "message" to "Enrollment confirmation email with QR code dispatched successfully."
            )
        )
    }
}

