package com.portocale.volunteer.users.service

interface QrService {
    fun getConfirmation(eventId: String, userId: String): ByteArray
}
