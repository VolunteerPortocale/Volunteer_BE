package com.example.demo.users

import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class User(val id: String?, val name: String, val age: Int)