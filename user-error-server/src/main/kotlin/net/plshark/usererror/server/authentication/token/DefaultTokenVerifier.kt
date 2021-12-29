package net.plshark.usererror.server.authentication.token

import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import net.plshark.usererror.authentication.token.UserErrorClaim
import net.plshark.usererror.authorization.UserAuthorities
import org.springframework.security.authentication.BadCredentialsException
import java.util.Optional

/**
 * Verifies JWTs
 */
class DefaultTokenVerifier(private val verifier: JWTVerifier) : TokenVerifier {

    override fun verifyToken(token: String): UserAuthorities {
        val jwt = decodeToken(token)
        return UserAuthorities(
            username = jwt.subject,
            authorities = parseAuthorities(jwt)
        )
    }

    override fun verifyRefreshToken(token: String): String {
        val jwt = decodeToken(token)
        val refresh = jwt.getClaim(UserErrorClaim.REFRESH).asBoolean()
        if (refresh == null || !refresh) throw BadCredentialsException("Token is not a refresh token")
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
        return Optional.ofNullable(jwt.getClaim(UserErrorClaim.AUTHORITIES).asList(String::class.java))
            .map { list -> list.toSet() }
            .orElse(emptySet())
    }
}
