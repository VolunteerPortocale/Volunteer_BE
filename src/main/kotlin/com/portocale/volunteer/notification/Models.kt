package com.portocale.volunteer.notification

enum class TemplateName(val value: String) {
    ENROLLMENT_CONFIRMATION("enrollmentConfirmation"),
    REGISTRATION("registration"),
}

enum class TemplateKeys(val value: String) {
    MESSAGES("messages"),
    LOCALE("locale"),
    SUBJECT("subject"),
    APP_LOGO("appLogo"),
    QR_CODE("qrCode"),
    OTP("otp"),
}

enum class EmailSubject(val value: String) {
    ENROLLMENT_CONFIRMATION("Enrollment confirmation"),
    REGISTRATION("Confirm your email"),
}
