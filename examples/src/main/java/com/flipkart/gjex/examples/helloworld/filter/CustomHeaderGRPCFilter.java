package com.flipkart.gjex.examples.helloworld.filter;

import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.logging.Logging;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;

import javax.inject.Named;

@Named("CustomHeaderGRPCFilter")
public class CustomHeaderGRPCFilter<Req extends GeneratedMessageV3, Res extends GeneratedMessageV3> extends GrpcFilter<Req, Res> implements Logging {

    @Override
    public void doProcessResponseHeaders(Metadata responseHeaders) {
        super.doProcessResponseHeaders(responseHeaders);
        responseHeaders.put(Metadata.Key.of("x-custom-header1", Metadata.ASCII_STRING_MARSHALLER), "value1");
    }

    @Override
    public void doProcessResponse(Res response) {


//        response = (Res) ((HelloReply) response).toBuilder().setMessage("Custom Header GRPC Filter").build();
        super.doProcessResponse(response);

        // Add custom header to response
//        response.toBuilder().setHeader("x-custom-header2", "value2");
    }

    @Override
    public GrpcFilter<Req, Res> getInstance() {
        return new CustomHeaderGRPCFilter<>();
    }
}
