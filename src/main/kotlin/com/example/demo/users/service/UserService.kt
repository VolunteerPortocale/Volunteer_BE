package com.example.demo.users.service

import com.example.demo.users.UserApi
import org.springframework.security.access.prepost.PreAuthorize

interface UserService {
    @PreAuthorize("hasRole('USER')")
    fun getAll(): List<UserApi>
    @PreAuthorize("hasRole('ADMIN')")
    fun getById(id: String): UserApi
    fun suspend(id: Int): UserApi // TODO: Teo - add suspend period
    fun delete(id: Int): Boolean
}