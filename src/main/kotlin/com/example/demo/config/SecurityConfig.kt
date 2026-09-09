package com.example.demo.config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() }
            //disabled csrf cause we use header based auth, not browser cookies

            .authorizeHttpRequests { auth ->
                auth
                    //allow actuator and swagger
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/actuator/**"
                    ).permitAll()

                    //protect endpoints by role (same role that assigned in db)
                    .requestMatchers("/api/v1/**").hasAnyRole("USER")

                    //all other endpoints need auth
                    .anyRequest().authenticated()
            }

            //enable basic auth (username/password)
            .httpBasic(Customizer.withDefaults())

        return http.build()
    }

    @Bean
    //for hashing rhe passwords
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
        val testUser: UserDetails = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("password123"))
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(testUser)
    }
}