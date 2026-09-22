package com.portocale.volunteer.users.repository

import com.portocale.volunteer.users.User
import com.portocale.volunteer.users.UserStatus
import java.util.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByEmailAndStatus(email: String, status: UserStatus = UserStatus.INACTIVE): Optional<User>
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
}
