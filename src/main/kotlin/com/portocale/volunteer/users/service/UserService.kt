package com.portocale.volunteer.users.service

import com.portocale.volunteer.users.UserApi
import org.springframework.security.access.prepost.PreAuthorize

interface UserService {
    @PreAuthorize("hasRole('USER')")
    fun getAll(): List<UserApi>

    @PreAuthorize("hasRole('ADMIN')")
    fun getById(id: String): UserApi
    fun suspend(id: Int): UserApi // Teo: add suspend period
    fun delete(id: Int): Boolean
}
