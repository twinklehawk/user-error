package net.plshark.users.auth.webservice;

import java.util.Objects;
import net.plshark.users.auth.model.AccountCredentials;
import net.plshark.users.auth.model.AuthToken;
import net.plshark.users.auth.model.AuthenticatedUser;
import net.plshark.users.auth.service.AuthService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = Objects.requireNonNull(authService);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AuthToken> authenticate(@RequestBody AccountCredentials credentials) {
        return authService.authenticate(credentials);
    }

    @PostMapping(value = "/refresh", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AuthToken> refresh(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }

    @PostMapping(value = "/validate", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AuthenticatedUser> validateToken(@RequestBody String accessToken) {
        return authService.validateToken(accessToken);
    }
}
