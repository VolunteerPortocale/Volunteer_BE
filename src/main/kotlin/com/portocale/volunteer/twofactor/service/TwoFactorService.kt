package com.portocale.volunteer.twofactor.service

import com.portocale.volunteer.twofactor.ConfirmTwoFactorApi
import com.portocale.volunteer.twofactor.DisableTwoFactorApi
import com.portocale.volunteer.twofactor.VerifyTwoFactorLoginApi
import com.portocale.volunteer.users.UserApi
import org.springframework.security.access.prepost.PreAuthorize

interface TwoFactorService {
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'NGO', 'VOLUNTEER')")
    fun initiateTwoFactorActivation(): Boolean

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'NGO', 'VOLUNTEER')")
    fun confirmTwoFactorActivation(input: ConfirmTwoFactorApi): UserApi

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'NGO', 'VOLUNTEER')")
    fun disableTwoFactor(input: DisableTwoFactorApi): UserApi

    fun verifyTwoFactorLogin(input: VerifyTwoFactorLoginApi): UserApi
}
