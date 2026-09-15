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
 * - `realm_access.roles`                     → ROLE_* authorities
 * - `new_keycloak_realm_access_roles`        → ROLE_* authorities (custom claim)
 * - `resource_access.<clientId>.roles`       → ROLE_* authorities (if clientId set)
 */
class CustomJwtConverter : Converter<Jwt, Principal> {

    /** Keycloak client id to look for in `resource_access`. */
    var resourceClientId: String? = null

    override fun convert(jwt: Jwt): Principal {
        val authorities = mutableListOf<GrantedAuthority>()

        // ── Standard realm roles ────────────────────────────────────
        val realmRoles: List<String> =
            (jwt.getClaim<Map<String, Any>>("realm_access")?.get("roles") as? List<*>)
                ?.mapNotNull { it?.toString() }
                ?: emptyList()

        authorities += realmRoles.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }

        // ── Custom realm roles claim ───────────────────────────────
        val customRealmRoles: List<String> =
            (jwt.getClaimAsStringList("new_keycloak_realm_access_roles") ?: emptyList())
                .mapNotNull { it?.toString() }

        authorities += customRealmRoles.map { SimpleGrantedAuthority("ROLE_${it.uppercase()}") }

        // ── Client roles ────────────────────────────────────────────
        val clientId = resourceClientId
        if (clientId != null) {
            val resourceAccess = jwt.getClaim<Map<String, Any>>("resource_access")
            val clientRoles: List<String> =
                ((resourceAccess?.get(clientId) as? Map<*, *>)?.get("roles") as? List<*>)
                    ?.mapNotNull { it?.toString() }
                    ?: emptyList()

            authorities += clientRoles.map { SimpleGrantedAuthority("ROLE_${clientId}_${it.uppercase()}") }
        }

        // ── Standard claims ────────────────────────────────────────
        val userId = jwt.subject ?: ""
        val email = jwt.getClaimAsString("email")
        val firstName = jwt.getClaimAsString("given_name")
        val lastName = jwt.getClaimAsString("family_name")

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
