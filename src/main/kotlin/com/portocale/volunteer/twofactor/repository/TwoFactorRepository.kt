package com.portocale.volunteer.twofactor.repository

import com.portocale.volunteer.twofactor.TwoFactor
import java.util.Optional
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TwoFactorRepository : MongoRepository<TwoFactor, String> {
    fun findByUserId(userId: String): Optional<TwoFactor>
}
