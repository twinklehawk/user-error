package net.plshark.auth.service;

import java.net.URI;
import java.util.Objects;
import com.auth0.jwt.JWTVerifier;
import net.plshark.auth.model.AccountCredentials;
import net.plshark.auth.model.AuthToken;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * AuthService implementation that sends requests to an auth server
 */
public class AuthServiceClient implements AuthService {

    private final WebClient webClient;
    private final JWTVerifier verifier;
    private final String baseUri;

    /**
     * Create a new instance
     * @param webClient the web client to use to make requests
     * @param verifier the verifier to use to verify tokens, only needs to have the public key set up if using keys
     * @param baseUri the base URI of the auth server
     */
    public AuthServiceClient(WebClient webClient, JWTVerifier verifier, String baseUri) {
        this.webClient = Objects.requireNonNull(webClient);
        this.verifier = Objects.requireNonNull(verifier);
        this.baseUri = Objects.requireNonNull(baseUri);
    }

    @Override
    public Mono<AuthToken> authenticate(AccountCredentials credentials) {
        return webClient.post()
                .uri(URI.create(baseUri + "/auth"))
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(credentials)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(response -> response.bodyToMono(AuthToken.class));
    }

    @Override
    public Mono<AuthToken> refresh(String refreshToken) {
        return webClient.post()
                .uri(URI.create(baseUri + "/auth/refresh"))
                .contentType(MediaType.TEXT_PLAIN)
                .syncBody(refreshToken)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(response -> response.bodyToMono(AuthToken.class));
    }

    @Override
    public Mono<Void> validateToken(String accessToken) {
        return Mono.defer(() -> {
            verifier.verify(accessToken);
            return Mono.empty();
        });
    }
}
