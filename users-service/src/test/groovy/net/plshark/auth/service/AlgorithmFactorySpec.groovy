package net.plshark.auth.service

import net.plshark.auth.AuthProperties
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
}
