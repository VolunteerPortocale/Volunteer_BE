package com.portocale.volunteer.twoFactor

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(name = "ConfirmTwoFactor")
data class ConfirmTwoFactorApi(
    @field:NotBlank
    val otp: String
)

@Schema(name = "DisableTwoFactor")
data class DisableTwoFactorApi(
    @field:NotBlank
    val password: String
)
