package net.plshark.users.auth.service

import net.plshark.users.auth.model.AccountCredentials
import net.plshark.users.auth.model.AuthToken
import net.plshark.users.auth.model.AuthenticatedUser
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI

/**
 * AuthService implementation that sends requests to an auth server
 */
class AuthServiceClient(private val webClient: WebClient, private val baseUri: String) : AuthService {

    override fun authenticate(credentials: AccountCredentials): Mono<AuthToken> {
        return webClient.post()
            .uri(URI.create("$baseUri/auth"))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(credentials)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(AuthToken::class.java)
    }

    override fun refresh(refreshToken: String): Mono<AuthToken> {
        return webClient.post()
            .uri(URI.create("$baseUri/auth/refresh"))
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue(refreshToken)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(AuthToken::class.java)
    }

    override fun validateToken(accessToken: String): Mono<AuthenticatedUser> {
        return webClient.post()
            .uri(URI.create("$baseUri/auth/validate"))
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue(accessToken)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(AuthenticatedUser::class.java)
    }
}
