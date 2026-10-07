package com.portocale.volunteer.twoFactor.service

import com.portocale.volunteer.twoFactor.ConfirmTwoFactorApi
import com.portocale.volunteer.twoFactor.DisableTwoFactorApi
import com.portocale.volunteer.twoFactor.VerifyTwoFactorLoginApi
import com.portocale.volunteer.users.UserApi
import org.springframework.security.access.prepost.PreAuthorize

interface TwoFactorService {

    @PreAuthorize("hasRole('ADMIN')")
    fun initiateTwoFactorActivation(userId: String): Boolean

    @PreAuthorize("hasRole('ADMIN')")
    fun confirmTwoFactorActivation(userId: String, input: ConfirmTwoFactorApi): Boolean

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'NGO', 'VOLUNTEER')")
    fun selfInitiateTwoFactorActivation(): Boolean

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'NGO', 'VOLUNTEER')")
    fun selfConfirmTwoFactorActivation(input: ConfirmTwoFactorApi): Boolean
}
