package net.plshark.users.auth.service

import com.google.common.collect.ImmutableList

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
}
