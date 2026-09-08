package com.example.demo.users.service

import com.example.demo.users.UserApi

interface UserService {
    fun getAll(): List<UserApi>
    fun getById(id: Int): UserApi
    fun suspend(id: Int): UserApi // TODO: Teo - add suspend period
    fun delete(id: Int): Boolean
}