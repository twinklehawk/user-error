package net.plshark.usererror.util

import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import java.util.concurrent.Callable

class ReactiveUtilsTest {

    @Test
    fun `the created mono signals a next item when the callable completes`() {
        val c = Callable { "test-val" }

        StepVerifier.create(ReactiveUtils.wrapWithMono(c))
            .expectNext("test-val")
            .verifyComplete()
    }

    @Test
    fun `the created mono signals an error if the callable fails`() {
        val c = Callable<String> { throw IllegalStateException() }

        StepVerifier.create(ReactiveUtils.wrapWithMono(c))
            .verifyError(IllegalStateException::class.java)
    }

    @Test
    fun `the created flux signals each item in the list when the callable completes`() {
        val c = Callable { listOf("test-val", "test-val-2") }

        StepVerifier.create(ReactiveUtils.wrapWithFlux(c))
            .expectNext("test-val")
            .expectNext("test-val-2")
            .verifyComplete()
    }

    @Test
    fun `the created flux signals an error if the callable fails`() {
        val c = Callable<List<String>> { throw IllegalStateException() }

        StepVerifier.create(ReactiveUtils.wrapWithFlux(c))
            .verifyError(IllegalStateException::class.java)
    }
}
