package com.portocale.volunteer.notification

enum class TemplateName(val value: String) {
    ENROLLMENT_CONFIRMATION("enrollmentConfirmation"),
    REGISTRATION("registration"),
    EVENT_REMINDER("eventReminder"),
    PASSWORD_RESET("passwordReset"),
}

enum class TemplateKeys(val value: String) {
    MESSAGES("messages"),
    LOCALE("locale"),
    SUBJECT("subject"),
    APP_LOGO("appLogo"),
    QR_CODE("qrCode"),
    OTP("otp"),
    EVENT_TITLE("eventTitle"),
    EVENT_LOCATION("eventLocation"),
    EVENT_START_TIME("eventStartTime"),
    TEMPORARY_PASSWORD("temporaryPassword"),
}

enum class EmailSubject(val value: String) {
    ENROLLMENT_CONFIRMATION("Enrollment confirmation"),
    REGISTRATION("Confirm your email"),
    EVENT_REMINDER("Event reminder"),
    PASSWORD_RESET("Password reset"),
}
