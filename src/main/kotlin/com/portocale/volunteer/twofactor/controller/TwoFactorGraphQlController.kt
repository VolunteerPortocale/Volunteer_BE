package com.portocale.volunteer.twofactor.controller

import com.portocale.volunteer.graphql.model.UserGQL
import com.portocale.volunteer.twofactor.ConfirmTwoFactorApi
import com.portocale.volunteer.twofactor.DisableTwoFactorApi
import com.portocale.volunteer.twofactor.VerifyTwoFactorLoginApi
import com.portocale.volunteer.twofactor.service.TwoFactorService
import com.portocale.volunteer.users.toUserGql
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class TwoFactorGraphQlController(
    private val twoFactorService: TwoFactorService
) {

    @MutationMapping
    fun initiateTwoFactor(): Boolean {
        return twoFactorService.initiateTwoFactorActivation()
    }

    @MutationMapping
    fun confirmTwoFactor(@Argument otp: String): UserGQL {
        return twoFactorService
            .confirmTwoFactorActivation(ConfirmTwoFactorApi(otp = otp))
            .toUserGql()
    }

    @MutationMapping
    fun disableTwoFactor(@Argument password: String): UserGQL {
        return twoFactorService
            .disableTwoFactor(DisableTwoFactorApi(password = password))
            .toUserGql()
    }

    @MutationMapping
    fun verifyTwoFactorLogin(
        @Argument email: String,
        @Argument otp: String
    ): UserGQL {
        return twoFactorService
            .verifyTwoFactorLogin(VerifyTwoFactorLoginApi(email = email, otp = otp))
            .toUserGql()
    }
}
