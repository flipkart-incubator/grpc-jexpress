package com.flipkart.gjex.examples.helloworld.filter;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.grpc.AccessLogGrpcFilter;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.google.protobuf.GeneratedMessageV3;

import javax.inject.Named;
import java.util.HashMap;

/**
 * CustomAccessLogFilter extending {@link AccessLogGrpcFilter}
 *
 * @author ajay.jalgaonkar
 */
@Named("CustomAccessLogFilter")
public class CustomAccessLogFilter extends AccessLogGrpcFilter {
    private static final String format = "{custom.className} {clientIp} {resourcePath} " +
        "{contentLength} {responseStatus} {responseTime}";
    private static final HashMap<String, String> customFields = new HashMap<>();

    @Override
    public void init() {
        setFormat(format);
        customFields.put("className", this.getClass().getSimpleName());
    }

    @Override
    public void doProcessRequest(GeneratedMessageV3 req, RequestParams requestParamsInput) {
        getAccessLogContextBuilder().customFields(customFields);
        super.doProcessRequest(req, requestParamsInput);
    }


    @Override
    public GrpcFilter getInstance() {
        return new CustomAccessLogFilter();
    }
}
