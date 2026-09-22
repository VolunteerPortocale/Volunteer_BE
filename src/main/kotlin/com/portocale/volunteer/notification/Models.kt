package com.portocale.volunteer.notification

enum class TemplateName(val value: String) {
    ENROLLMENT_CONFIRMATION("enrollmentConfirmation"),
}

enum class TemplateKeys(val value: String) {
    MESSAGES("messages"),
    LOCALE("locale"),
    SUBJECT("subject"),
    APP_LOGO("appLogo"),
    QR_CODE("qrCode"),
}

enum class EmailSubject(val value: String) {
    ENROLLMENT_CONFIRMATION("Enrollment confirmation"),
}
