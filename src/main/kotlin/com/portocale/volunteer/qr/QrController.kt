package com.portocale.volunteer.qr
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/qr")
@Tag(name = "QR", description = "Endpoints for generating event QR codes")
class QrController(
    private val qrService: QrService
) {

    @GetMapping(value = ["", "/generate"], produces = [MediaType.IMAGE_PNG_VALUE])
    @Operation(summary = "Generate and stream QR code image for eventId and userId")
    fun getQrCode(
        @RequestParam(defaultValue = "demo-event") eventId: String,
        @RequestParam(defaultValue = "demo-user") userId: String
    ): ResponseEntity<ByteArray> {
        val imageBytes = qrService.getOrCreateAndRender(eventId, userId)
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(imageBytes)
    }
}
