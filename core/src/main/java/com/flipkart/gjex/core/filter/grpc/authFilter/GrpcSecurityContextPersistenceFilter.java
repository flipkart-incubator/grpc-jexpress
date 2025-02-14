package com.flipkart.gjex.core.filter.grpc.authFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.gjex.core.GJEXObjectMapper;
import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.kloud.filter.SecurityContext;
import com.flipkart.kloud.filter.SecurityContextHolder;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcSecurityContextPersistenceFilter<Req extends GeneratedMessageV3, Res extends GeneratedMessageV3>
    extends GrpcFilter<Req, Res> implements Logging {

    private static final Logger logger = LoggerFactory.getLogger(GrpcSecurityContextPersistenceFilter.class);
    private final Metadata.Key<String> SECURITY_CONTEXT_KEY;
    private final String sessionKey;
    private ObjectMapper objectMapper;


    public GrpcSecurityContextPersistenceFilter() {
        this("AUTHN_SECURITY_CONTEXT");
    }

    public GrpcSecurityContextPersistenceFilter(String sessionKey) {
        this.sessionKey = sessionKey;
        if (this.objectMapper == null) {
            this.objectMapper = GJEXObjectMapper.newObjectMapper();
        }
        this.SECURITY_CONTEXT_KEY = Metadata.Key.of(sessionKey, Metadata.ASCII_STRING_MARSHALLER);
    }

    @Override
    public void doProcessRequest(Req request, RequestParams<Metadata> requestParams) {
        SecurityContextHolder.clearSecurityContext();
        Metadata metadata = requestParams.getMetadata();
        boolean var11 = false;

        try {
            var11 = true;
            SecurityContext securityContext = retrieveSecurityContext(metadata);
            if (securityContext != null) {
                SecurityContextHolder.setSecurityContext(securityContext);
            }

            var11 = false;
        } finally {
            if (var11) {
                SecurityContext finalContext = SecurityContextHolder.getSecurityContext();
                SecurityContextHolder.clearSecurityContext();
                storeSecurityContext(metadata, finalContext);
            }
        }

        SecurityContext securityContext = SecurityContextHolder.getSecurityContext();
        SecurityContextHolder.clearSecurityContext();
        storeSecurityContext(metadata, securityContext);
    }

    @Override
    public void doProcessResponseHeaders(Metadata responseHeaders) {
    }

    @Override
    public void doHandleException(Exception e) {
        logger.warn("Handling exception in gRPC: {}", e.getMessage(), e);
    }

    private SecurityContext retrieveSecurityContext(Metadata metadata) {
        try {
            String context = metadata.get(SECURITY_CONTEXT_KEY);
            return context == null ? null : objectMapper.readValue(context, SecurityContext.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void storeSecurityContext(Metadata metadata, SecurityContext securityContext) {
        try {
            metadata.put(SECURITY_CONTEXT_KEY, objectMapper.writeValueAsString(securityContext));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GrpcFilter<Req, Res> getInstance() {
        return new GrpcSecurityContextPersistenceFilter<>(this.sessionKey);
    }
}
