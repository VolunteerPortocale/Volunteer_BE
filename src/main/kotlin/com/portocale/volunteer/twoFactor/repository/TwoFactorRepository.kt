package com.portocale.volunteer.twoFactor.repository

import com.portocale.volunteer.twoFactor.TwoFactor
import java.util.Optional
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TwoFactorRepository : MongoRepository<TwoFactor, String> {
    fun findByUserId(userId: String): Optional<TwoFactor>
}
