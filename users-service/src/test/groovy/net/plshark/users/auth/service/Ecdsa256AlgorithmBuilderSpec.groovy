package net.plshark.users.auth.service

import net.plshark.users.auth.AuthProperties
import spock.lang.Specification

class Ecdsa256AlgorithmBuilderSpec extends Specification{

    def builder = new Ecdsa256AlgorithmBuilder()

    def 'should load the keystore from the filesystem and access the correct key for ECDSA256'() {
        def location = AlgorithmFactorySpec.class.getResource('test-store.jks').file
        def props = AuthProperties.forKeystore(Ecdsa256AlgorithmBuilder.ECDSA256, 'bad-users', 1000,
                new AuthProperties.Keystore('pkcs12', location, 'test-pass'),
                new AuthProperties.Key('test-key', 'test-pass'))

        when:
        def algorithm = builder.build(props)

        then:
        algorithm.name == 'ES256'
    }

    def 'should fail if the keystore is null'() {
        def props = AuthProperties.forKeystore(Ecdsa256AlgorithmBuilder.ECDSA256, 'bad-users', 1000,
                null, new AuthProperties.Key('test-key', 'test-pass'))

        when:
        builder.build(props)

        then:
        thrown(IllegalStateException)
    }

    def 'should fail if the key is null'() {
        def location = AlgorithmFactorySpec.class.getResource('test-store.jks').file
        def props = AuthProperties.forKeystore(Ecdsa256AlgorithmBuilder.ECDSA256, 'bad-users', 1000,
                new AuthProperties.Keystore('pkcs12', location, 'test-pass'), null)

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
