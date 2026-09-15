package com.portocale.volunteer.qr.service

interface QrService {
    fun generateVolunteerPresenceConfirmationQr(eventId: String, userId: String): ByteArray
}
