package com.portocale.volunteer.config.jwt

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

/**
 * Converts a Keycloak-issued JWT into a typed [Principal].
 *
 * Extracts:
 * - `sub`                                    → userId
 * - `email`                                  → email
 * - `given_name`                             → firstName
 * - `family_name`                            → lastName
 * - `roles`        → ROLE_* authorities (custom claim)
 */
class CustomJwtConverter : Converter<Jwt, Principal> {

    override fun convert(jwt: Jwt): Principal {
        val authorities = mutableListOf<GrantedAuthority>()


        val roles: List<String> =
            (jwt.getClaimAsStringList("role") ?: emptyList())
                .mapNotNull { it }

        authorities += roles.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }

        // ── Standard claims ────────────────────────────────────────

        val userId = jwt.getClaimAsString("external_id") ?: error(IllegalStateException("Missing id"))
        val email = jwt.getClaimAsString("email") ?: error(IllegalStateException("Missing email"))
        val firstName = jwt.getClaimAsString("given_name") ?: error(IllegalStateException("Missing given name"))
        val lastName = jwt.getClaimAsString("family_name") ?: error(IllegalStateException("Missing family name"))

        return Principal(
            userId = userId,
            email = email,
            firstName = firstName,
            lastName = lastName,
            rawJwt = jwt,
            authorities = authorities
        )
    }
}
