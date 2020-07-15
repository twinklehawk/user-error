package net.plshark.users.auth.service

import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import net.plshark.users.auth.model.AuthenticatedUser
import org.springframework.security.authentication.BadCredentialsException
import java.util.*

/**
 * Verifies JWTs
 */
class DefaultTokenVerifier(private val verifier: JWTVerifier) : TokenVerifier {

    override fun verifyToken(token: String): AuthenticatedUser {
        val jwt = decodeToken(token)
        return AuthenticatedUser(
            username = jwt.subject,
            authorities = parseAuthorities(jwt)
        )
    }

    override fun verifyRefreshToken(token: String): String {
        val jwt = decodeToken(token)
        val refresh = jwt.getClaim(PlsharkClaim.REFRESH).asBoolean()
        if (refresh == null || !refresh)
            throw BadCredentialsException("Token is not a refresh token")
        return jwt.subject
    }

    /**
     * Validate and decode a token
     * @param token the token
     * @return the decoded token if successful or a BadCredentialsException if the token is invalid
     */
    private fun decodeToken(token: String): DecodedJWT {
        try {
            return verifier.verify(token)
        } catch (e: JWTVerificationException) {
            throw BadCredentialsException("Invalid credentials", e)
        }
    }

    /**
     * Parse a list of authorities from a decoded JWT
     * @param jwt the decoded JWT
     * @return the list of authorities from the token
     */
    private fun parseAuthorities(jwt: DecodedJWT): Set<String> {
        return Optional.ofNullable(jwt.getClaim(PlsharkClaim.AUTHORITIES).asList(String::class.java))
            .map { list -> list.toSet() }
            .orElse(emptySet())
    }
}
