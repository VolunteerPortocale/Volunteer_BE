package com.portocale.volunteer.users.keycloak

import com.portocale.volunteer.users.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/kc/users")
class KeycloakInternalController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<KcUserApi> =
        userRepository.findById(id)
            .map { ResponseEntity.ok(it.toKcUserApi()) }
            .orElse(ResponseEntity.notFound().build())

    @GetMapping(params = ["email"])
    fun getByEmail(@RequestParam email: String): ResponseEntity<KcUserApi> =
        userRepository.findByEmail(email.trim().lowercase())
            .map { ResponseEntity.ok(it.toKcUserApi()) }
            .orElse(ResponseEntity.notFound().build())

    @PostMapping("/verify")
    fun verify(@RequestBody request: KcVerifyRequest): KcVerifyResponse {
        val user = userRepository.findByEmail(request.username.trim().lowercase()).orElse(null)
        val valid = user != null &&
            user.isLoginAllowed() &&
            passwordEncoder.matches(request.password, user.passwordHash)
        return KcVerifyResponse(valid)
    }
}
