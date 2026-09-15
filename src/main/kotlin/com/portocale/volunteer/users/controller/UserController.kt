package com.portocale.volunteer.users.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.portocale.volunteer.users.UserApi
import com.portocale.volunteer.users.service.QrService
import com.portocale.volunteer.users.service.UserService

@RestController
@Tag(name = "Users", description = "Group of endpoints for user management and confirmation QR codes")
class UserController(
    private val userService: UserService,
    private val qrService: QrService
) {

    @GetMapping("/api/v1/users")
    @Operation(summary = "Get all users")
    fun getAll(): List<UserApi> {
        return userService.getAll()
    }

    @GetMapping("/api/v1/users/{id}")
    @Operation(summary = "Get user by Id")
    @ApiResponses(
        value = [ApiResponse(
            description = "OK",
            responseCode = "200",
            content = [Content(schema = Schema(implementation = UserApi::class))]
        )]
    )
    fun getById(@PathVariable id: String): UserApi {
        return userService.getById(id)
    }

    @GetMapping(
        value = [
            "/api/v1/qr",
            "/api/v1/qr/generate",
            "/api/v1/users/generate",
            "/api/v1/users/{userId}/events/{eventId}/confirmation"
        ],
        produces = [MediaType.IMAGE_PNG_VALUE]
    )
    @Operation(summary = "Generate and stream QR code image for eventId and userId")
    fun getConfirmationQr(
        @RequestParam(defaultValue = "demo-event") eventId: String,
        @RequestParam(defaultValue = "demo-user") userId: String
    ): ResponseEntity<ByteArray> {
        val imageBytes = qrService.getConfirmation(eventId, userId)
        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(imageBytes)
    }
}
