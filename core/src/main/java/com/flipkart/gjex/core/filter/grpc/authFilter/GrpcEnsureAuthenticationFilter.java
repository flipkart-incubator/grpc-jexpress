package com.flipkart.gjex.core.filter.grpc.authFilter;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.kloud.authn.AuthenticationException;
import com.flipkart.kloud.filter.SecurityContextHolder;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class GrpcEnsureAuthenticationFilter<Req extends GeneratedMessageV3, Res extends GeneratedMessageV3>
    extends GrpcFilter<Req, Res> implements Logging {

    private static final Logger logger = LoggerFactory.getLogger(GrpcEnsureAuthenticationFilter.class.getName());

    private String ignorePatternStr = null;
    private String loginUrl;
    private boolean saveRequest;

    public GrpcEnsureAuthenticationFilter(String loginUrl, String ignorePattern, boolean saveRequest) {
        this.saveRequest = saveRequest;
        this.ignorePatternStr = ignorePattern;
        this.loginUrl = loginUrl;
    }


    @Override
    public void doProcessRequest(Req request, RequestParams<Metadata> requestParams) {
        Metadata headers = requestParams.getMetadata();
        String pathInfo = requestParams.getResourcePath();

        Pattern ignorePattern = ignorePatternStr != null ? Pattern.compile(ignorePatternStr) : null;
        if (ignorePattern == null || (pathInfo != null && !ignorePattern.matcher(pathInfo).matches())) {
            if (SecurityContextHolder.getSecurityContext().getUser() != null) {
                return;
            } else {
                if (this.loginUrl == null) {
                    doHandleException(new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("This call needs to be authenticated")));
                }

                if (this.saveRequest) {
                    headers.put(Metadata.Key.of("ORIGINAL_URL", Metadata.ASCII_STRING_MARSHALLER), pathInfo);
                }

                doHandleException(new StatusRuntimeException(Status.UNAUTHENTICATED));
            }
        }
    }

    @Override
    public void doHandleException(Exception e) {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("REDIRECT_URL", Metadata.ASCII_STRING_MARSHALLER), "Login url:: " + loginUrl);

        String errorMessage = e.getMessage();
        if (StringUtils.isBlank(errorMessage)) {
            errorMessage = "Call not authenticated";
        }

        throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription(errorMessage), metadata);
    }


    @Override
    public GrpcFilter<Req, Res> getInstance() {
        return new GrpcEnsureAuthenticationFilter<>(loginUrl, ignorePatternStr, saveRequest);
    }
}
