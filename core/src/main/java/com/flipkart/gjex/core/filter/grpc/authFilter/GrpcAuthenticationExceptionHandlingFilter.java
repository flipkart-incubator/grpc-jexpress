package com.flipkart.gjex.core.filter.grpc.authFilter;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.kloud.authn.AuthenticationException;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GrpcAuthenticationExceptionHandlingFilter<Req extends GeneratedMessageV3, Res extends GeneratedMessageV3>
    extends GrpcFilter<Req, Res> implements Logging {

    private static final Logger logger = LoggerFactory.getLogger(GrpcAuthenticationExceptionHandlingFilter.class.getName());

    @Override
    public void doProcessRequest(Req request, RequestParams<Metadata> requestParams) {
    }

    @Override
    public void doHandleException(Exception e) {
        logger.warn("Handling AuthenticationException in gRPC: {}", e.getMessage(), e);
        String errorMessage = findRootCause(e).getMessage();
        if (StringUtils.isBlank(errorMessage)) {
            errorMessage = "Authentication failed";
        }
        throw new AuthenticationException(errorMessage);
    }

    private static Throwable findRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }


    @Override
    public GrpcFilter<Req, Res> getInstance() {
        return new GrpcAuthenticationExceptionHandlingFilter<>();
    }
}
