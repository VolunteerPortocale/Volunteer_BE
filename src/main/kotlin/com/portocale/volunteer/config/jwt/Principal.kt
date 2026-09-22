package com.portocale.volunteer.config.jwt

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken

/**
 * Typed authentication token extracted from a Keycloak-issued JWT.
 *
 * Extends [AbstractOAuth2TokenAuthenticationToken] so Spring Security handles
 * credential erasure correctly (principal is a plain String, not `this`).
 *
 * Downstream code casts the SecurityContext authentication directly:
 * ```kotlin
 * val auth = SecurityContextHolder.getContext().authentication
 * val principal = auth as KeycloakPrincipal
 * println("userId: ${principal.userId}, email: ${principal.email}")
 * ```
 *
 * The raw [Jwt] is available via [getToken] (inherited).
 */
class Principal(
    /** Keycloak `sub` claim — stable user identifier. */
    val userId: String,

    /** User's email from the `email` claim. */
    val email: String,

    /** User's first name from the `given_name` claim. */
    val firstName: String,

    /** User's last name from the `family_name` claim. */
    val lastName: String,

    rawJwt: Jwt,
    authorities: Collection<GrantedAuthority>
) : AbstractOAuth2TokenAuthenticationToken<Jwt>(
    rawJwt,
    userId,
    rawJwt.tokenValue,
    authorities
) {

    init {
        isAuthenticated = true
    }

    override fun getName(): String = userId

    override fun getTokenAttributes(): Map<String, Any> = token.claims

    /** Convenience: full name if both parts exist. */
    val displayName: String = "$firstName $lastName"

    /** Check if user has a specific realm role (e.g. "admin", "user"). */
    fun hasRealmRole(role: String): Boolean =
        authorities.any { it.authority == "ROLE_${role.uppercase()}" }

    /** Check if user has a specific client role. */
    fun hasClientRole(clientId: String, role: String): Boolean =
        authorities.any { it.authority == "ROLE_${clientId}_${role.uppercase()}" }
}
