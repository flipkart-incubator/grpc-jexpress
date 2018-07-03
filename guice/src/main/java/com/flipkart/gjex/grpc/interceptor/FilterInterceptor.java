/*
 * Copyright 2012-2016, the original author or authors.
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
package com.flipkart.gjex.grpc.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Singleton;

import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.filter.MethodFilters;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.gjex.core.util.Pair;
import com.google.protobuf.GeneratedMessageV3;

import io.grpc.BindableService;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

/**
 * An implementation of the gRPC {@link ServerInterceptor} that allows custom {@link Filter} instances to be invoked around relevant methods to process Request, Request-Headers, Response and 
 * Response-Headers data.  
 * 
 * @author regu.b
 *
 */
@Singleton
@Named("FilterInterceptor")
public class FilterInterceptor implements ServerInterceptor, Logging {
	
	/** List of Filter instances*/
	@SuppressWarnings("rawtypes")
	private Map<String,List<Filter>> filtersMap = new HashMap<String, List<Filter>>();	
	
	@SuppressWarnings("rawtypes")
	public void registerFilters(List<Filter> filters, List<BindableService> services) {
		Map<Class<?>, Filter> classToInstanceMap = filters.stream()
				.collect(Collectors.toMap(Object::getClass, Function.identity()));
		services.forEach(service -> {
			this.getAnnotatedMethods(service.getClass(),MethodFilters.class).forEach(pair -> {
				List<Filter> filtersForMethod = new LinkedList<Filter>();
				Arrays.asList(pair.getValue().getAnnotation(MethodFilters.class).value()).forEach(filterClass -> {
					if (!classToInstanceMap.containsKey(filterClass)) {
						throw new RuntimeException("Filter instance not bound for Filter class :" + filterClass.getName());
					}
					filtersForMethod.add(classToInstanceMap.get(filterClass));
				});
				filtersMap.put((service.bindService().getServiceDescriptor().getName() + "/" + pair.getValue().getName()).toLowerCase(),
						filtersForMethod);
			});
		});
	}
		
	@Override
	public <ReqT , RespT > Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
			ServerCallHandler<ReqT, RespT> next) {
		ServerCall.Listener<ReqT> listener;
		try {
	        listener = new SimpleForwardingServerCallListener<ReqT>(next.startCall (
	        		new SimpleForwardingServerCall<ReqT, RespT>(call) {
	        			@Override
	        		    public void sendMessage(final RespT response) {
	        				super.sendMessage(response);
	        			}
	        			@Override
	        		    public void sendHeaders(final Metadata headers) {
	        				super.sendHeaders(headers);
	        			}
	        		}, headers)) {
	        		@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
	        		public void onMessage(ReqT request) {
	        			List<Filter> filters = filtersMap.get(call.getMethodDescriptor().getFullMethodName().toLowerCase());
	        			filters.forEach(filter -> { 
	        				try {
	        					filter.doFilterRequest((GeneratedMessageV3)request, headers);
	        				} catch (StatusRuntimeException se) {
	        					call.close(se.getStatus(), se.getTrailers());
	        					return;
	        				}
	        			});
		        	    super.onMessage(request);
	        		}
	       };
	    } catch (Throwable ex) {
	        error ("Uncaught exception from grpc service");
	        call.close (Status.INTERNAL
	                .withCause (ex)
	                .withDescription ("Uncaught exception from grpc service"), null);
	        return new ServerCall.Listener<ReqT>() {};
	    }		
		return listener;
	}

	/**
	 * Helper method to get annotated methods on a Class. Navigates up the superclass hierarchy to get the methods. This is required when used with DI mechanisms like Guice that
	 * create a CGLIB proxy sub-type for instances and annotations are not copied to the sub-type.
	 * Cannot use @Inherited annotation as a workaround because it applies only to Type/Class level annotations and not for Method-level ones.
	 * @see https://github.com/google/guice/issues/101
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Pair<?,Method>> getAnnotatedMethods(Class<?> cls, Class<? extends Annotation> anno) {
		List<Pair<?,Method>> methods = new LinkedList<Pair<?,Method>>();
		for (Method m : cls.getDeclaredMethods()) {
			if (m.getAnnotation(anno) != null) {
				methods.add(new Pair(cls,m));
			}
		}
		if (methods.isEmpty()) {
			Class<?> superCls = cls.getSuperclass();
			return (superCls != null) ? getAnnotatedMethods(superCls, anno) : null;
		}
		return methods;
	}
	
	/*
	private static class MethoAnnotationPair {
		final Method method;
		@SuppressWarnings("rawtypes")
		final Class<? extends Filter>[] filterClasses;
		@SuppressWarnings("rawtypes")
		public MethoAnnotationPair(Method method, Class<? extends Filter>[] filterClasses) {
			super();
			this.method = method;
			this.filterClasses = filterClasses;
		}
		public Method getMethod() {
			return method;
		}
		
		@SuppressWarnings("rawtypes")
		public List<Class<? extends Filter>> getFilterClassList() {
			return Arrays.asList(filterClasses);
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public void registerFilters1(List<Filter> filters, List<BindableService> services) {
		
		Map<Method, List<Class<? extends Filter>>> methodToFilterClassMap = services.stream()
				.map(service -> getAnnotatedMethods(service.getClass(),MethodFilters.class))
				.flatMap(Collection::stream)
				.map(pair -> new MethoAnnotationPair(pair.getValue(), pair.getValue().getAnnotation(MethodFilters.class).value()))
				.collect(Collectors.toMap(MethoAnnotationPair::getMethod, MethoAnnotationPair::getFilterClassList));

		Map<Class<?>, Filter> classToInstanceMap = filters.stream()
				.collect(Collectors.toMap(Object::getClass, Function.identity()));

		Predicate<Class> classInstanceFound =  classToInstanceMap::containsKey;
		
		Function<List<Class<? extends Filter>>, List<Filter>> classListToInstanceList = (classes) -> {
			List<Class<? extends Filter>> invalidClass = classes.stream().filter(classInstanceFound.negate()).collect(Collectors.toList());
			if(!invalidClass.isEmpty())
				throw new RuntimeException("Instances not initialized for filter classed - "+ invalidClass);
			return classes.stream().map(classToInstanceMap::get).collect(Collectors.toList());
		};

		this.filtersMap = methodToFilterClassMap.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, entry -> classListToInstanceList.apply(entry.getValue())));
		info(methodToFilterInstanceMap.toString());
	
	}
	*/
	
	
	
}
