package com.flipkart.gjex.core.filter.grpc.authFilter;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.kloud.authn.AuthenticationException;
import com.flipkart.kloud.authn.JWTClaimsValidator;
import com.flipkart.kloud.authn.JWTValidationService;
import com.flipkart.kloud.authn.SystemTokenJWTClaimsValidator;
import com.flipkart.kloud.authn.User;
import com.flipkart.kloud.authn.UserTokenJWTClaimsValidator;
import com.flipkart.kloud.filter.AuthenticationSuccessHandler;
import com.flipkart.kloud.filter.SecurityContext;
import com.flipkart.kloud.filter.SecurityContextHolder;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class GrpcSystemUserAuthenticationFilter<Req extends GeneratedMessageV3, Res extends  GeneratedMessageV3>
    extends GrpcFilter<Req, Res> implements Logging {

    private final List<JWTValidationService> jwtValidationServices;
    private AuthenticationSuccessHandler successHandler = null;
    private final String ignorePatternStr;
    private final String clientId;
    private final List<String> whiteListedClientIds;
    private final List<String> authnUrls;
    private static final Logger logger = LoggerFactory.getLogger(GrpcSystemUserAuthenticationFilter.class);


    public GrpcSystemUserAuthenticationFilter() {
        this(new ArrayList<>(), null, null, null);
    }

    public GrpcSystemUserAuthenticationFilter(String authnUrl, String clientId, String ignorePattern) {
        this(Collections.singletonList(authnUrl), clientId, ignorePattern);
    }

    public GrpcSystemUserAuthenticationFilter(List<String> authnUrls, String clientId, String ignorePattern) {
        this(authnUrls, null, clientId, ignorePattern);
    }

    public GrpcSystemUserAuthenticationFilter(List<String> authnUrls, List<String> whiteListedClientIds, String clientId, String ignorePattern) {
        this.jwtValidationServices = new ArrayList<>();
        this.clientId = clientId;
        this.whiteListedClientIds = whiteListedClientIds;
        this.authnUrls = authnUrls;
        this.ignorePatternStr = ignorePattern;
        for (String authnUrl : authnUrls) {
            this.jwtValidationServices.add(new JWTValidationService(authnUrl, Arrays.<JWTClaimsValidator>asList(new SystemTokenJWTClaimsValidator(clientId), new UserTokenJWTClaimsValidator(whiteListedClientIds))));
        }
    }


    @Override
    public void doProcessRequest(Req req, RequestParams<Metadata> requestParamsInput) {
        Metadata metadata = requestParamsInput.getMetadata();
        String authorizationHeader = metadata.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER));

        Pattern ignorePattern = ignorePatternStr != null ? Pattern.compile(ignorePatternStr) : null;
        if ((ignorePattern == null || requestParamsInput.getResourcePath() != null && !ignorePattern.matcher(requestParamsInput.getResourcePath()).matches()) &&  authorizationHeader != null) {
            String token = extractBearerToken(authorizationHeader);
            if (token != null) {
                try {
                    validateToken(token);
                    logger.info("Call validated");
                } catch (AuthenticationException e) {
                    doHandleException(e);
                }
            }
        }
    }

    /**
     * Placeholder method for processing response headers. Currently, does not perform any operations.
     *
     * @param responseHeaders The metadata associated with the gRPC response.
     */
    @Override
    public void doProcessResponseHeaders(Metadata responseHeaders) {}

    /**
     * Processes the outgoing gRPC response by logging relevant request and response details.
     * Logs the client IP, requested resource path, size of the response message, and the time taken to process the request.
     *
     * @param response The outgoing gRPC response message.
     */
    @Override
    public void doProcessResponse(Res response) {}

    /**
     * Handles exceptions that occur during the processing of the request or response.
     * Logs the exception details along with the request and response context.
     *
     * @param e The exception that occurred.
     */
    @Override
    public void doHandleException(Exception e) {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("ErrorMessage", Metadata.ASCII_STRING_MARSHALLER), e.getMessage());

        String errorMessage = e.getMessage();
        if (StringUtils.isBlank(errorMessage)) {
            errorMessage = "Authentication failed";
        }

        throw new StatusRuntimeException(Status.PERMISSION_DENIED.withDescription(errorMessage), metadata);
    }


    private void validateToken(String token) throws AuthenticationException {
        Iterator<JWTValidationService> iterator = jwtValidationServices.iterator();
        StringBuilder exception = new StringBuilder();
        while (iterator.hasNext()) {
            try {
                JWTValidationService jwtValidationService = iterator.next();
                User user = jwtValidationService.validateAndGetUser(token);
                SecurityContextHolder.setSecurityContext(new SecurityContext(user));
                if (successHandler != null) {
                    this.successHandler.onAuthenticationSuccess(null, null, user);
                }
                return;
            } catch (Exception e) {
                exception.append(e.getMessage());
            }
        }
        throw new AuthenticationException(exception.toString());
    }

    private String extractBearerToken(String authorizationHeader) {
        String prefix = "Bearer";

        if (authorizationHeader.startsWith(prefix)) {
            int commaIndex = authorizationHeader.indexOf(44);
            authorizationHeader = authorizationHeader.substring(prefix.length()).trim();
            return commaIndex != -1 ? authorizationHeader.substring(0, commaIndex) : authorizationHeader;
        } else {
            return null;
        }

    }

    @Override
    public GrpcFilter<Req, Res> getInstance() {
        return new GrpcSystemUserAuthenticationFilter<>(authnUrls, whiteListedClientIds, clientId, ignorePatternStr);
    }
}
