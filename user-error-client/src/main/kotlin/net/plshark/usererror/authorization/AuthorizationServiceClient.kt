package net.plshark.usererror.authorization

import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URI

class AuthorizationServiceClient(private val webClient: WebClient, private val baseUri: String) : AuthorizationService {

    override suspend fun validateToken(accessToken: String): AuthenticatedUser {
        // TODO needs authentication, remove from this class
        return webClient.post()
            .uri(URI.create("$baseUri/auth/validate"))
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue(accessToken)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
    }
}
