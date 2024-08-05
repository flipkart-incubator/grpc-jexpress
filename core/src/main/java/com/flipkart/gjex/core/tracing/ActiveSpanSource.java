/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.gjex.core.tracing;

import com.flipkart.gjex.core.context.GJEXContext;

import io.opentracing.Span;

/**
 * An interface that defines how to get the current active span.
 * Ported from https://github.com/grpc-ecosystem/grpc-opentracing/blob/master/java/src/main/java/io/opentracing/contrib/ActiveSpanSource.java
 */
public interface ActiveSpanSource {

    /**
    * ActiveSpanSource implementation that always returns
    *  null as the active span
    */
    public static ActiveSpanSource NONE = new ActiveSpanSource() {
        @Override
        public Span getActiveSpan() {
            return null;
        }
    };

    /**
    * ActiveSpanSource implementation that returns the
    *  current span stored in the GRPC context under
    *  {@link GJEXContextKey}
    */
    public static ActiveSpanSource GRPC_CONTEXT = new ActiveSpanSource() {
        @Override
        public Span getActiveSpan() {
            return GJEXContext.activeSpan();
        }
    };

    /**
    * @return the active span
    */
    public Span getActiveSpan();
}
