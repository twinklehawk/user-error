package net.plshark.usererror.authorization

import net.plshark.usererror.authentication.token.TokenVerifier
import org.springframework.stereotype.Component

/**
 * Default authorization service server side implementation
 */
@Component
class AuthorizationServiceImpl(private val tokenVerifier: TokenVerifier) : AuthorizationService {

    override suspend fun validateToken(accessToken: String): AuthenticatedUser {
        return tokenVerifier.verifyToken(accessToken)
    }
}
