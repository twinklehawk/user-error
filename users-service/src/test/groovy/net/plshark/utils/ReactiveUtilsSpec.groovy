package net.plshark.utils

import java.util.concurrent.Callable
import reactor.test.StepVerifier
import spock.lang.Specification

class ReactiveUtilsSpec extends Specification {

    def 'the created mono signals a next item when the callable completes'() {
        Callable<String> c = { 'test-val' }

        expect:
        StepVerifier.create(ReactiveUtils.wrapWithMono(c))
                .expectNext('test-val')
                .verifyComplete()
    }

    def 'the created mono signals an error if the callable fails'() {
        Callable<String> c = { throw new RuntimeException() }

        expect:
        StepVerifier.create(ReactiveUtils.wrapWithMono(c))
                .verifyError(RuntimeException.class)
    }

    def 'the created flux signals each item in the list when the callable completes'() {
        Callable<List<String>> c = { Arrays.asList('test-val', 'test-val-2') }

        expect:
        StepVerifier.create(ReactiveUtils.wrapWithFlux(c))
                .expectNext('test-val')
                .expectNext('test-val-2')
                .verifyComplete()
    }

    def 'the created flux signals an error if the callable fails'() {
        Callable<List<String>> c = { throw new RuntimeException() }

        expect:
        StepVerifier.create(ReactiveUtils.wrapWithFlux(c))
                .verifyError(RuntimeException.class)
    }
}
