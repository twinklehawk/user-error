package net.plshark.usererror.authentication.token

import net.plshark.usererror.authentication.AccountCredentials
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URI

/**
 * AuthService implementation that sends requests to an auth server
 */
class AuthenticationServiceClient(private val webClient: WebClient, private val baseUri: String) :
    AuthenticationService {

    override suspend fun authenticate(credentials: AccountCredentials): AuthToken {
        return webClient.post()
            .uri(URI.create("$baseUri/auth"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(credentials)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
    }

    override suspend fun refresh(refreshToken: String): AuthToken {
        return webClient.post()
            .uri(URI.create("$baseUri/auth/refresh"))
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue(refreshToken)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
    }
}
