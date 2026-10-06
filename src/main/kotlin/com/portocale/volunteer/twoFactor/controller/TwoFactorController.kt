package com.portocale.volunteer.twoFactor.controller

import com.portocale.volunteer.twoFactor.ConfirmTwoFactorApi
import com.portocale.volunteer.twoFactor.service.TwoFactorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "TwoFactor", description = "Endpoints for two-factor authentication")
@RequestMapping("/api/v1/two-factor")
class TwoFactorController(
    private val twoFactorService: TwoFactorService
) {

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Initiate 2FA activation handshake")
    fun initiateTwoFactor(
        @PathVariable userId: String
    ): Boolean {
        return twoFactorService.initiateTwoFactorActivation(userId)
    }

    @PostMapping("/{userId}/confirm")
    @Operation(summary = "Confirm 2FA activation with OTP")
    fun confirmTwoFactor(
        @PathVariable userId: String,
        @Valid @RequestBody input: ConfirmTwoFactorApi
    ): Boolean {
        return twoFactorService.confirmTwoFactorActivation(userId, input)
    }
}
