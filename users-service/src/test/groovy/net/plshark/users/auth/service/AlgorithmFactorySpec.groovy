package net.plshark.users.auth.service

import net.plshark.users.auth.AuthProperties
import spock.lang.Specification

class AlgorithmFactorySpec extends Specification {

    def props = new AuthProperties()
    def factory = new AlgorithmFactory()

    def 'should throw an IllegalArgumentException if the algorithm name is not recognized'() {
        when:
        props.algorithm = 'made up name'
        factory.buildAlgorithm(props)

        then:
        thrown(IllegalArgumentException)
    }

    def 'should load the keystore from the filesystem and access the correct key for ECDSA256'() {
        props.algorithm = AlgorithmFactory.ECDSA256
        props.keystore.location = AlgorithmFactorySpec.class.getResource('test-store.jks').file
        props.keystore.password = 'test-pass'
        props.keystore.type = 'pkcs12'
        props.key.password = 'test-pass'
        props.key.alias = 'test-key'

        when:
        def algorithm = factory.buildAlgorithm(props)

        then:
        algorithm.name == 'ES256'
    }

    def 'none should load the none algorithm'() {
        props.algorithm = AlgorithmFactory.NONE

        when:
        def algorithm = factory.buildAlgorithm(props)

        then:
        algorithm.name == 'none'
    }

    def 'hmac256 should load the corresponding algorithm'() {
        props.algorithm = AlgorithmFactory.HMAC256
        props.secret = 'test-secret'

        when:
        def algorithm = factory.buildAlgorithm(props)

        then:
        algorithm.name == 'HS256'
    }

    def 'hmac256 should fail if there is no secret set'() {
        props.algorithm = AlgorithmFactory.HMAC256

        when:
        factory.buildAlgorithm(props)

        then:
        thrown(IllegalStateException)
    }

    def 'hmac512 should load the corresponding algorithm'() {
        props.algorithm = AlgorithmFactory.HMAC512
        props.secret = 'test-secret'

        when:
        def algorithm = factory.buildAlgorithm(props)

        then:
        algorithm.name == 'HS512'
    }

    def 'hmac512 should fail if there is no secret set'() {
        props.algorithm = AlgorithmFactory.HMAC512

        when:
        factory.buildAlgorithm(props)

        then:
        thrown(IllegalStateException)
    }
}
