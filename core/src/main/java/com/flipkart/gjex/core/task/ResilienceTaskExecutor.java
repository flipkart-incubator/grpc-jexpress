package com.flipkart.gjex.core.task;

import com.flipkart.gjex.core.logging.Logging;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.metrics.Timer;

import io.grpc.Context;
import io.reactivex.functions.BiConsumer;
import org.aopalliance.intercept.MethodInvocation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class ResilienceTaskExecutor<T> implements FutureProvider<T>, Logging {

    /** The MethodInvocation to execute asynchronously*/
    private final MethodInvocation invocation;

    /** The currently active gRPC Context*/
    private Context currentContext;

    /** The completion BiConsumer*/
    private BiConsumer<T, Throwable> completionConsumer;

    /** Indicates if requests may be hedged within the configured timeout duration*/
    private boolean withRequestHedging;

    /** The rolling tail latency as seen by Hystrix*/
    private long rollingTailLatency;

    private int timeout;

    private final CircuitBreaker circuitBreaker;
    private final ThreadPoolBulkhead threadPoolBulkhead;
    private final Timer timer;

    public ResilienceTaskExecutor(MethodInvocation invocation,
                                  CircuitBreaker circuitBreaker,
                                  ThreadPoolBulkhead bulkhead,
                                  Timer timer,
                                  Boolean withRequestHedging,
                                  int timeout
    ) {
        currentContext = Context.current();
        this.invocation = invocation;
        this.circuitBreaker = circuitBreaker;
        this.timer = timer;
        this.threadPoolBulkhead = bulkhead;
        this.withRequestHedging = withRequestHedging;
        this.timeout = timeout;
    }

    public ResilienceTaskExecutor<T> clone() {
        ResilienceTaskExecutor<T> clone =
                new ResilienceTaskExecutor<T>(
                        this.invocation,
                        this.circuitBreaker,
                        this.threadPoolBulkhead,
                        this.timer,
                        this.withRequestHedging,
                        this.timeout
                );
        return clone;
    }

    public void setCompletionConsumer(BiConsumer<T, Throwable> completionConsumer) {
        this.completionConsumer = completionConsumer;
    }

    public MethodInvocation getInvocation() {
        return invocation;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isWithRequestHedging() {
        return withRequestHedging;
    }

    public long getRollingTailLatency() {
        //TODO: hardcoded value kept here for now.
        // Need to figure out a way to get rolling 95th percentile latency in resilience
        return 10000L;
    }

    @Override
    public String getName() {
        return getInvocation().getMethod().getName();
    }

    public CompletableFuture<T> getFuture() {
        Supplier<T> supplier = prepareBaseSupplier();
        return decorateSupplierWithResilience(supplier)
                    .get()
                    .toCompletableFuture();
    }

    private Supplier<T> prepareBaseSupplier() {
        return () -> {
            try {
                return run();
            } catch (Exception e) {
                error("Error executing task", e);
                throw new RuntimeException(e);
            }
        };
    }

    public T run() throws Exception {
        Context previous = this.currentContext.attach(); // setting the current gRPC context for the executing Hystrix thread
        Throwable error = null;
        T result = null;
        try {
            result = ((AsyncResult<T>)this.invocation.proceed()).invoke(); // call the AsyncResult#invoke() to execute the actual work to be performed asynchronously
            return result;
        } catch (Throwable e) {
            error = e;
            error("Error executing task", e);
            throw new RuntimeException(e);
        } finally {
            if (this.completionConsumer != null) {
                this.completionConsumer.accept(result, error); // inform the completion status to the registered completion consumer
            }
            this.currentContext.detach(previous); // unset the current gRPC context
        }
    }

    public <T> Supplier<CompletionStage<T>> decorateSupplierWithResilience(Supplier<T> baseSupplier) {
        Supplier<T> cbSupplier = circuitBreaker.decorateSupplier(baseSupplier);
        Supplier<CompletionStage<T>> tpbSupplier = threadPoolBulkhead.decorateSupplier(cbSupplier);
        return Timer.decorateCompletionStageSupplier(timer, tpbSupplier);
    }
    
}
