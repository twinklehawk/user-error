package net.plshark.usererror.client.authorization.token

import net.plshark.usererror.authorization.UserAuthorities
import net.plshark.usererror.authorization.token.TokenAuthorizationService
import net.plshark.usererror.client.UserErrorClientConfig
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URI

@Component
class TokenAuthorizationServiceClient(
    private val webClient: WebClient,
    config: UserErrorClientConfig
) : TokenAuthorizationService {

    private val baseUrl: String

    init {
        baseUrl = config.baseUrl
    }

    override suspend fun getAuthoritiesForToken(accessToken: String): UserAuthorities {
        return webClient.post()
            .uri(URI.create("$baseUrl/authorization/authorities"))
            .contentType(MediaType.TEXT_PLAIN)
            .bodyValue(accessToken)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody()
    }
}
