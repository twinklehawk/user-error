package net.plshark.users.auth.service

import com.google.common.collect.ImmutableList
import net.plshark.users.auth.AuthProperties
import spock.lang.Specification

class AlgorithmFactorySpec extends Specification {

    def factory = new AlgorithmFactory(ImmutableList.of())

    def 'should throw an IllegalArgumentException if the algorithm name is not recognized'() {
        def props = AuthProperties.forSecret('made up name', 'bad-users', 1000, 'secret')

        when:
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
}
