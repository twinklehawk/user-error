package net.plshark.users.auth.service;

import java.util.Date;
import java.util.Objects;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * Creates auth and refresh JWTs
 */
public class DefaultTokenBuilder implements TokenBuilder {

    private final Algorithm algorithm;
    private final String issuer;

    /**
     * Create a new instance
     * @param algorithm the algorithm to use to sign tokens
     * @param issuer the issuer to include in the generated tokens
     */
    public DefaultTokenBuilder(Algorithm algorithm, String issuer) {
        this.algorithm = Objects.requireNonNull(algorithm);
        this.issuer = Objects.requireNonNull(issuer);
    }

    @Override
    public String buildAccessToken(String username, long expirationMs, String[] authorities) {
        return buildBaseToken(username, expirationMs)
                .withArrayClaim(AuthService.AUTHORITIES_CLAIM, authorities)
                .sign(algorithm);
    }

    @Override
    public String buildRefreshToken(String username, long expirationMs) {
        return buildBaseToken(username, expirationMs)
                .withClaim(AuthService.REFRESH_CLAIM, true)
                .sign(algorithm);
    }

    /**
     * Build a token with all the common fields already set
     * @param username the user the token is for
     * @param expirationMs the number of milliseconds from now until the token should expire
     * @return the token
     */
    private JWTCreator.Builder buildBaseToken(String username, long expirationMs) {
        Date now = new Date();
        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + expirationMs));
    }
}
