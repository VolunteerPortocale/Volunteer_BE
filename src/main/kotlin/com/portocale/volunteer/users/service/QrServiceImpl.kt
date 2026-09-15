package com.portocale.volunteer.users.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.portocale.volunteer.qr.EventQr
import com.portocale.volunteer.qr.EventQrRepository
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

@Service
class QrServiceImpl(
    private val repository: EventQrRepository
) : QrService {

    private val frontendBaseUrl = "https://volunteer-fe.amanemisalovereaddeathnote.workers.dev"

    override fun getConfirmation(eventId: String, userId: String): ByteArray {
        val link = "$frontendBaseUrl/?eventId=$eventId&userId=$userId"

        if (!repository.existsByEventIdAndUserId(eventId, userId)) {
            repository.save(EventQr(eventId = eventId, userId = userId, payload = link))
        }

        val bitMatrix = QRCodeWriter().encode(link, BarcodeFormat.QR_CODE, 300, 300)
        return ByteArrayOutputStream().use { out ->
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out)
            out.toByteArray()
        }
    }
}
