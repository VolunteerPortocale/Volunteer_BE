package com.example.demo.config

import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            .cors { cors -> cors.disable() }

            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/actuator/**"
                    ).permitAll()

                    // Secure endpoints by role or allow any authenticated user
                    .requestMatchers("/api/v1/**").authenticated()

                    .anyRequest().authenticated()
            }
            .httpBasic(Customizer.withDefaults())

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(
        environment: Environment,
        passwordEncoder: PasswordEncoder
    ): UserDetailsService {

        val credentials = Binder.get(environment)
            .bind("users.credentials", Bindable.listOf(Map::class.java))
            .orElse(emptyList()) ?: emptyList()

        val userDetailsList = credentials.map { userMap ->
            val username = userMap["username"] as? String ?: ""
            val rawPassword = userMap["password"] as? String ?: ""

            val roles = when (val rawRoles = userMap["roles"]) {
                is List<*> -> rawRoles.mapNotNull { it?.toString() }
                is String -> listOf(rawRoles)
                else -> emptyList()
            }

            User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .roles(*roles.toTypedArray())
                .build()
        }

        return InMemoryUserDetailsManager(userDetailsList)
    }
}