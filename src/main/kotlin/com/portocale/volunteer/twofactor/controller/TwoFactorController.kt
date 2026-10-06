package com.portocale.volunteer.twofactor.controller

import com.portocale.volunteer.twofactor.ConfirmTwoFactorApi
import com.portocale.volunteer.twofactor.service.TwoFactorService
import com.portocale.volunteer.users.UserApi
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "TwoFactor", description = "Endpoints for two-factor authentication")
@RequestMapping("/api/v1/users/2fa")
class TwoFactorController(
    private val twoFactorService: TwoFactorService
) {

    @PostMapping("/initiate")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Initiate 2FA activation handshake")
    fun initiateTwoFactor(): Boolean {
        return twoFactorService.initiateTwoFactorActivation()
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm 2FA activation with OTP")
    fun confirmTwoFactor(@Valid @RequestBody input: ConfirmTwoFactorApi): UserApi {
        return twoFactorService.confirmTwoFactorActivation(input)
    }
}
