package com.flipkart.gjex.core.task;

import io.reactivex.functions.BiConsumer;

import java.util.concurrent.Future;

public interface FutureProvider<T> extends HasExecutionProperties {

    Future<T> getFuture();

    FutureProvider<T> clone();

    void setCompletionConsumer(BiConsumer<T, Throwable> completionConsumer);

}
