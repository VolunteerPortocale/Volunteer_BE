package com.portocale.volunteer.users.service

import com.portocale.volunteer.users.UserApi
import com.portocale.volunteer.users.UserNotFoundException
import com.portocale.volunteer.users.repository.UserRepository
import com.portocale.volunteer.users.toUserApi
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {
    override fun getAll(): List<UserApi> {
        return userRepository.findAll()
            .map { user -> user.toUserApi() }
    }

    override fun getById(id: String): UserApi {
        return userRepository.findById(id)
            .map { it.toUserApi() }
            .orElseThrow { UserNotFoundException() }
    }

    override fun suspend(id: Int): UserApi {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

}
