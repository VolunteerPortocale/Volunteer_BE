package com.portocale.volunteer.twoFactor.controller

import com.portocale.volunteer.config.jwt.Principal
import com.portocale.volunteer.graphql.model.UserGQL
import com.portocale.volunteer.twoFactor.ConfirmTwoFactorApi
import com.portocale.volunteer.twoFactor.DisableTwoFactorApi
import com.portocale.volunteer.twoFactor.service.TwoFactorService
import com.portocale.volunteer.users.toUserGql
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class TwoFactorGraphQlController(
    private val twoFactorService: TwoFactorService
) {

    @MutationMapping
    fun initiateTwoFactor(
        principal: Principal
    ): Boolean {
        return twoFactorService.initiateTwoFactorActivation(principal.userId)
    }

    @MutationMapping
    fun confirmTwoFactor(
        @Argument otp: String,
        principal: Principal
    ): Boolean {
        return twoFactorService
            .confirmTwoFactorActivation(principal.userId, ConfirmTwoFactorApi(otp = otp))
    }

    @MutationMapping
    fun disableTwoFactor(
        @Argument password: String
    ): Boolean {
        return twoFactorService.selfDisableTwoFactor(DisableTwoFactorApi(password = password))
    }
}
