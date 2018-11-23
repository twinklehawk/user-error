package net.plshark.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import spock.lang.Specification

class DefaultTokenBuilderSpec extends Specification {

    def algorithm = Algorithm.HMAC256('test-secret')
    def issuer = 'test-issuer'
    def builder = new DefaultTokenBuilder(algorithm, issuer)

    def 'access tokens should include the username, issuer, authorities, and expiration'() {
        when:
        def token = builder.buildAccessToken('test-user', 1000, ['role1', 'role2'] as String[])
        def decodedToken = JWT.decode(token)

        then:
        decodedToken.subject == 'test-user'
        decodedToken.expiresAt != null
        decodedToken.issuer == 'test-issuer'
        decodedToken.getClaim(AuthService.AUTHORITIES_CLAIM).asList(String.class).size() == 2
    }

    def 'refresh tokens should include the username, issuer, refresh claim, and expiration'() {
        when:
        def token = builder.buildRefreshToken('test-user', 1000)
        def decodedToken = JWT.decode(token)

        then:
        decodedToken.subject == 'test-user'
        decodedToken.expiresAt != null
        decodedToken.issuer == 'test-issuer'
        !decodedToken.getClaim(AuthService.REFRESH_CLAIM).isNull()
    }
}
