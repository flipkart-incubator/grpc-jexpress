/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.flipkart.gjex.core.context;

import com.flipkart.gjex.core.tracing.TracingSampler;

import io.grpc.Context;
import io.grpc.Metadata;
import io.opentracing.Span;
import io.opentracing.SpanContext;

/**
 * Code ported from {@linkplain https://github.com/opentracing-contrib/java-grpc/blob/master/src/main/java/io/opentracing/contrib/grpc/OpenTracingContextKey.java}
 *
 * Supports storing and propagating useful per-execution data such as current OpenTracing trace state using the gRPC {@link io.grpc.Context}
 *
 */
public class GJEXContext {

	public static final String KEY_ROOT_SPAN_NAME = "io.opentracing.root-span";
	public static final String KEY_ACTIVE_SPAN_NAME = "io.opentracing.active-span";
	public static final String KEY_CONTEXT_NAME = "io.opentracing.active-span-context";
	public static final String KEY_TRACING_SAMPLER_NAME = "io.opentracing.active-tracing-sampler";
	public static final String KEY_HEADERS_NAME = "com.flipkart.gjex.headers";

	private static final Context.Key<Span> KEY_ROOT_SPAN = Context.key(KEY_ROOT_SPAN_NAME);
	private static final Context.Key<Span> KEY_ACTIVE_SPAN = Context.key(KEY_ACTIVE_SPAN_NAME);
	private static final Context.Key<SpanContext> KEY_CONTEXT = Context.key(KEY_CONTEXT_NAME);
	private static final Context.Key<TracingSampler> KEY_TRACING_SAMPLER = Context.key(KEY_TRACING_SAMPLER_NAME);
	private static final Context.Key<Metadata> KEY_HEADERS = Context.key(KEY_HEADERS_NAME);

	/**
	 * @return the OpenTracing context key for Root span
	 */
	public static Context.Key<Span> getKeyRoot() {
		return KEY_ROOT_SPAN;
	}
	/**
	 * @return the active root span for the current request
	 */
	public static Span activeRootSpan() {
		return KEY_ROOT_SPAN.get();
	}
	/**
	 * @return the OpenTracing context key for Active span
	 */
	public static Context.Key<Span> getKeyActiveSpan() {
		return KEY_ACTIVE_SPAN;
	}
	/**
	 * @return the active span for the current request
	 */
	public static Span activeSpan() {
		return KEY_ACTIVE_SPAN.get();
	}

	/**
	 * @return the OpenTracing context key for span context
	 */
	public static Context.Key<SpanContext> getSpanContextKey() {
		return KEY_CONTEXT;
	}
	public static SpanContext activeSpanContext() {
		return KEY_CONTEXT.get();
	}

	/**
	 * @return the GJEX TracingSampler key and active sampler
	 */
	public static Context.Key<TracingSampler> getTracingSamplerKey() {
		return KEY_TRACING_SAMPLER;
	}
	public static TracingSampler activeTracingSampler() {
		return KEY_TRACING_SAMPLER.get();
	}

	/**
	 * @return the GJEX headers key and forwarded headers
	 */
	public static Context.Key<Metadata> getHeadersKey() {
		return KEY_HEADERS;
	}
	public static Metadata activeHeaders() {
		return KEY_HEADERS.get();
	}

}
