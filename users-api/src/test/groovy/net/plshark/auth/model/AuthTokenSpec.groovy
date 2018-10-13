package net.plshark.auth.model

import spock.lang.Specification

class AuthTokenSpec extends Specification {

    def 'Builder creates the correct object and has a default token type'() {
        when:
        AuthToken token = new AuthToken.Builder().accessToken('token').expiresIn(100)
                .refreshToken('refresh').scope('scope').build()

        then:
        token.accessToken == 'token'
        token.expiresIn == 100
        token.refreshToken == 'refresh'
        token.scope == 'scope'
        token.tokenType == AuthToken.DEFAULT_TOKEN_TYPE
    }
}
