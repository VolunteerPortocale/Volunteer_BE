package com.portocale.volunteer.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.security.MessageDigest
import org.springframework.web.filter.OncePerRequestFilter

class KeycloakApiKeyFilter(private val apiKey: String) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val provided = request.getHeader("X-KC-Api-Key").orEmpty()
        val ok = apiKey.isNotBlank() &&
            MessageDigest.isEqual(provided.toByteArray(), apiKey.toByteArray())

        if (!ok) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }
        filterChain.doFilter(request, response)
    }
}
