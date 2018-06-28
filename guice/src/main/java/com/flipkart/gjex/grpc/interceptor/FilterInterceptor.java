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

import java.util.LinkedList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.logging.Logging;
import com.google.protobuf.GeneratedMessageV3;

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

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
	private List<Filter> filters = new LinkedList<Filter>();
	
	public void registerFilters(@SuppressWarnings("rawtypes") List<Filter> filters) {
		this.filters.addAll(filters);
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
	        		@SuppressWarnings("unchecked")
				@Override
	        		public void onMessage(ReqT request) {
	        			info("Method to be invoked : " + call.getMethodDescriptor().getFullMethodName());
	        			filters.forEach(filter -> filter.doFilterRequest((GeneratedMessageV3)request));
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

	/*
	call.close (Status.PERMISSION_DENIED
            .withDescription ("Authorization failure!!!"), new Metadata());
	return new ServerCall.Listener<ReqT>() {};
	*/

}
