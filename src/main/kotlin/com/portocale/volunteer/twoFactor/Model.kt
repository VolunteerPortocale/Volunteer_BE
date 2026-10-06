package com.portocale.volunteer.twoFactor

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

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

@Schema(name = "VerifyTwoFactorLogin")
data class VerifyTwoFactorLoginApi(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    @field:Size(min = 6, max = 6)
    val otp: String
)
