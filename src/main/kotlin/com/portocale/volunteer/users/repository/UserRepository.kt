package com.portocale.volunteer.users.repository

import com.portocale.volunteer.users.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String>
