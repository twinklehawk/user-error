package net.plshark.auth.jwt

import org.springframework.security.core.authority.SimpleGrantedAuthority
import spock.lang.Specification

class JwtAuthenticationTokenSpec extends Specification {

    def 'builder should create correct token'() {
        when:
        def token = JwtAuthenticationToken.builder()
                .withUsername('username')
                .withToken('test-token')
                .withAuthenticated(true)
                .withAuthorities(['auth1', 'auth2'])
                .build()

        then:
        token.name == 'username'
        token.principal == 'username'
        token.credentials == 'test-token'
        token.authenticated
        token.authorities.size() == 2
        token.authorities.contains(new SimpleGrantedAuthority('auth1'))
        token.authorities.contains(new SimpleGrantedAuthority('auth2'))
    }
}
