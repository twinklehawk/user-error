package net.plshark.users.auth.service

import net.plshark.users.auth.AuthProperties
import spock.lang.Specification

class HmacAlgorithmBuilderSpec extends Specification{

    def builder = new HmacAlgorithmBuilder()

    def 'hmac256 should load the correct algorithm'() {
        def props = AuthProperties.forSecret(HmacAlgorithmBuilder.HMAC256, 'bad-users', 1000, 'secret')

        when:
        def algorithm = builder.build(props)

        then:
        algorithm.name == 'HS256'
    }

    def 'hmac512 should load the correct algorithm'() {
        def props = AuthProperties.forSecret(HmacAlgorithmBuilder.HMAC512, 'bad-users', 1000, 'secret')

        when:
        def algorithm = builder.build(props)

        then:
        algorithm.name == 'HS512'
    }

    def 'should fail if the secret is null'() {
        def props = new AuthProperties(HmacAlgorithmBuilder.HMAC256, 'bad-users', 1000, null, null, null)

        when:
        builder.build(props)

        then:
        thrown(IllegalStateException)
    }

    def 'should return null if the name is anything else'() {
        def props = AuthProperties.forNone('bad-users', 1000)

        expect:
        builder.build(props) == null
    }
}
