package net.plshark.usererror.server.authorization.token

import net.plshark.usererror.authorization.UserAuthorities
import net.plshark.usererror.authorization.token.TokenAuthorizationService
import net.plshark.usererror.server.authentication.token.TokenVerifier
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authorization")
class TokenAuthorizationController(private val tokenVerifier: TokenVerifier) : TokenAuthorizationService {

    @PostMapping(
        value = ["/authorities"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    override suspend fun getAuthoritiesForToken(accessToken: String): UserAuthorities {
        return tokenVerifier.verifyToken(accessToken)
    }
}
