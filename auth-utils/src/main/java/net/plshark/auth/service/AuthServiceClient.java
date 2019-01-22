package net.plshark.auth.service;

import java.net.URI;
import java.util.Objects;
import net.plshark.auth.model.AccountCredentials;
import net.plshark.auth.model.AuthToken;
import net.plshark.auth.model.AuthenticatedUser;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * AuthService implementation that sends requests to an auth server
 */
public class AuthServiceClient implements AuthService {

    private final WebClient webClient;
    private final String baseUri;

    /**
     * Create a new instance
     * @param webClient the web client to use to make requests
     * @param baseUri the base URI of the auth server
     */
    public AuthServiceClient(WebClient webClient, String baseUri) {
        this.webClient = Objects.requireNonNull(webClient);
        this.baseUri = Objects.requireNonNull(baseUri);
    }

    @Override
    public Mono<AuthToken> authenticate(AccountCredentials credentials) {
        return webClient.post()
                .uri(URI.create(baseUri + "/auth"))
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(credentials)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AuthToken.class);
    }

    @Override
    public Mono<AuthToken> refresh(String refreshToken) {
        return webClient.post()
                .uri(URI.create(baseUri + "/auth/refresh"))
                .contentType(MediaType.TEXT_PLAIN)
                .syncBody(refreshToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AuthToken.class);
    }

    @Override
    public Mono<AuthenticatedUser> validateToken(String accessToken) {
        return webClient.post()
                .uri(URI.create(baseUri + "/auth/validate"))
                .contentType(MediaType.TEXT_PLAIN)
                .syncBody(accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AuthenticatedUser.class);
    }
}
