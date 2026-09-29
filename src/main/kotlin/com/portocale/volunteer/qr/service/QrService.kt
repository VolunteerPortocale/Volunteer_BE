package com.portocale.volunteer.qr.service

interface QrService {
    fun generateVolunteerPresenceConfirmationQr(enrollmentId: String): ByteArray
}
