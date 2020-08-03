package net.plshark.users.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

/**
 * Creates auth and refresh JWTs
 */
class DefaultTokenBuilder(private val algorithm: Algorithm, private val issuer: String) : TokenBuilder {

    override fun buildAccessToken(username: String, expirationMs: Long, authorities: Array<String>): String {
        return buildBaseToken(username, expirationMs)
            .withArrayClaim(PlsharkClaim.AUTHORITIES, authorities)
            .sign(algorithm)
    }

    override fun buildRefreshToken(username: String, expirationMs: Long): String {
        return buildBaseToken(username, expirationMs)
            .withClaim(PlsharkClaim.REFRESH, true)
            .sign(algorithm)
    }

    /**
     * Build a token with all the common fields already set
     * @param username the user the token is for
     * @param expirationMs the number of milliseconds from now until the token should expire
     * @return the token
     */
    private fun buildBaseToken(username: String, expirationMs: Long): JWTCreator.Builder {
        val now = Date()
        return JWT.create()
            .withSubject(username)
            .withIssuer(issuer)
            .withIssuedAt(now)
            .withExpiresAt(Date(now.time + expirationMs))
    }
}
