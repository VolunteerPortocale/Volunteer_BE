package com.portocale.volunteer.qr.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import org.springframework.stereotype.Service

private const val QR_CODE_SIZE = 300

@Service
class QrServiceImpl : QrService {

    override fun generateVolunteerPresenceConfirmationQr(enrollmentId: String): ByteArray {
        val encodedEnrollment = Base64.encode(enrollmentId.toByteArray())

        val bitMatrix = QRCodeWriter().encode(encodedEnrollment, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE)
        return ByteArrayOutputStream().use { out ->
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out)
            out.toByteArray()
        }
    }
}
