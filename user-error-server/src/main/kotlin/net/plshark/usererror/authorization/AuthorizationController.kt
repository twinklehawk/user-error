package net.plshark.usererror.authorization

import net.plshark.usererror.authentication.token.TokenVerifier
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authorization")
class AuthorizationController(private val tokenVerifier: TokenVerifier) : AuthorizationService {

    @PostMapping(
        value = ["/validate"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    override suspend fun validateToken(accessToken: String): AuthenticatedUser {
        return tokenVerifier.verifyToken(accessToken)
    }
}
