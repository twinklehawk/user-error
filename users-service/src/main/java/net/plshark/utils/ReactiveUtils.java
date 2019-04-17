package net.plshark.utils;

import java.util.List;
import java.util.concurrent.Callable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Utilities for working with Reactive
 */
public class ReactiveUtils {

    /**
     * Wrap a synchronous operation that returns a single value in a Mono
     * @param <T> the operation return type
     * @param callable the synchronous operation
     * @return a wrapped synchronous operation
     */
    public static <T> Mono<T> wrapWithMono(Callable<T> callable) {
        Mono<T> blockingWrapper = Mono.fromCallable(callable);
        return blockingWrapper.subscribeOn(Schedulers.elastic())
                .publishOn(Schedulers.parallel());
    }

    /**
     * Wrap a synchronous operation that returns multiple values in a Flux
     * @param <T> the operation return type
     * @param callable the synchronous operation
     * @return the wrapped synchronous operation
     */
    public static <T> Flux<T> wrapWithFlux(Callable<List<T>> callable) {
        Mono<List<T>> mono = wrapWithMono(callable);
        return mono.flatMapMany(Flux::fromIterable);
    }
}
