package com.portocale.volunteer.qr.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream

private const val QR_CODE_SIZE = 300

@Service
class QrServiceImpl(
    @Value("\${volunteer.volunteerPresenceConfirmationUlr}")
    private val volunteerPresenceConfirmationUlr: String
) : QrService {

    override fun generateVolunteerPresenceConfirmationQr(eventId: String, userId: String): ByteArray {
        val link = volunteerPresenceConfirmationUlr.format(eventId, userId)

        val bitMatrix = QRCodeWriter().encode(link, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE)
        return ByteArrayOutputStream().use { out ->
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out)
            out.toByteArray()
        }
    }
}
