package net.plshark.users.auth.jwt

import org.springframework.security.core.authority.SimpleGrantedAuthority
import spock.lang.Specification

class JwtAuthenticationTokenSpec extends Specification {

    def 'builder should create correct token'() {
        when:
        def token = JwtAuthenticationToken.builder()
                .username('username')
                .token('test-token')
                .authenticated(true)
                .authority('auth1')
                .authority('auth2')
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
