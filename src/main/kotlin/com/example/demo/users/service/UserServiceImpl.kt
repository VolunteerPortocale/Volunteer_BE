package com.example.demo.users.service

import com.example.demo.users.UserApi
import com.example.demo.users.repository.UserRepository
import com.example.demo.users.toUserApi
import org.springframework.security.core.context.SecurityContextHolder
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
        SecurityContextHolder.getContext().authentication?.let {
            it
        }
        return userRepository.findById(id)
            .map { it.toUserApi() }
            .orElseThrow { RuntimeException("User not found") }
    }

    override fun suspend(id: Int): UserApi {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

}