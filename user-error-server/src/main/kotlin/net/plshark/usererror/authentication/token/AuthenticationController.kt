package net.plshark.usererror.authentication.token

import net.plshark.usererror.authentication.AccountCredentials
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthenticationController(private val authService: AuthenticationService) {

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun authenticate(@RequestBody credentials: AccountCredentials): AuthToken {
        return authService.authenticate(credentials)
    }

    @PostMapping(
        value = ["/refresh"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun refresh(@RequestBody refreshToken: String): AuthToken {
        return authService.refresh(refreshToken)
    }
}
