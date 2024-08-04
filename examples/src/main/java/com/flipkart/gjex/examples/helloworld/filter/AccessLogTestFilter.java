package com.flipkart.gjex.examples.helloworld.filter;

import com.flipkart.gjex.core.filter.grpc.AccessLogGrpcFilter;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.google.protobuf.GeneratedMessageV3;

/**
 * Test filter to override {@link AccessLogGrpcFilter}
 * by binding in {@link com.flipkart.gjex.examples.helloworld.guice.HelloWorldModule}
 */
public class AccessLogTestFilter extends AccessLogGrpcFilter {

    @Override
    public void doProcessResponse(GeneratedMessageV3 response) {
        logger.info("from AccessLogTestFilter");
        super.doProcessResponse(response);
    }

    @Override
    public GrpcFilter getInstance() {
        return new AccessLogTestFilter();
    }
}
