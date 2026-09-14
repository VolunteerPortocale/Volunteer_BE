package com.portocale.volunteer.users

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "User")
data class UserApi(val id: String?, val name: String, val age: Int)
