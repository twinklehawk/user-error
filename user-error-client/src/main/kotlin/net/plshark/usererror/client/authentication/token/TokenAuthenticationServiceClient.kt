package net.plshark.usererror.client.authentication.token

import net.plshark.usererror.authentication.AccountCredentials
import net.plshark.usererror.authentication.token.AuthToken
import net.plshark.usererror.authentication.token.TokenAuthenticationService
import net.plshark.usererror.client.UserErrorClientConfig
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URI

/**
 * AuthService implementation that sends requests to an auth server
 */
@Component
class TokenAuthenticationServiceClient(
    private val webClient: WebClient,
    config: UserErrorClientConfig
) : TokenAuthenticationService {

    private val baseUrl: String

    init {
        baseUrl = config.baseUrl
    }

    override suspend fun authenticate(credentials: AccountCredentials): AuthToken {
        return webClient.post()
            .uri(URI.create("$baseUrl/auth"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(credentials)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
    }

    override suspend fun refresh(refreshToken: String): AuthToken {
        return webClient.post()
            .uri(URI.create("$baseUrl/auth/refresh"))
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue(refreshToken)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
    }
}
