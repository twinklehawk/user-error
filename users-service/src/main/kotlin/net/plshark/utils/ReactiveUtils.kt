package net.plshark.utils

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Callable

/**
 * Utilities for working with Reactive
 */
object ReactiveUtils {
    /**
     * Wrap a synchronous operation that returns a single value in a Mono
     * @param <T> the operation return type
     * @param callable the synchronous operation
     * @return a wrapped synchronous operation
     */
    fun <T> wrapWithMono(callable: Callable<T>): Mono<T> {
        val blockingWrapper = Mono.fromCallable(callable)
        return blockingWrapper.subscribeOn(Schedulers.boundedElastic())
            .publishOn(Schedulers.parallel())
    }

    /**
     * Wrap a synchronous operation that returns multiple values in a Flux
     * @param <T> the operation return type
     * @param callable the synchronous operation
     * @return the wrapped synchronous operation
     */
    fun <T> wrapWithFlux(callable: Callable<List<T>>): Flux<T> {
        val mono = wrapWithMono(callable)
        return mono.flatMapMany { Flux.fromIterable(it!!) }
    }
}
