package com.flipkart.gjex.core.filter.grpc.authFilter;

import com.flipkart.gjex.core.filter.RequestParams;
import com.flipkart.gjex.core.filter.grpc.GrpcFilter;
import com.flipkart.gjex.core.logging.Logging;
import com.flipkart.kloud.authn.AuthenticationException;
import com.flipkart.kloud.authn.IstioUser;
import com.flipkart.kloud.authn.User;
import com.flipkart.kloud.authn.XfccParser;
import com.flipkart.kloud.filter.SecurityContext;
import com.flipkart.kloud.filter.SecurityContextHolder;
import com.google.protobuf.GeneratedMessageV3;
import io.grpc.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class GrpcIstioCertAuthenticationFilter<Req extends GeneratedMessageV3, Res extends GeneratedMessageV3>
    extends GrpcFilter<Req, Res> implements Logging {

    private static final Logger logger = LoggerFactory.getLogger(GrpcIstioCertAuthenticationFilter.class);
    private static final Metadata.Key<String> XFCC_HEADER_KEY = Metadata.Key.of("X-Forwarded-Client-Cert", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public void doProcessRequest(Req request, RequestParams<Metadata> requestParams) {
        Metadata headers = requestParams.getMetadata();
        String clientCert = headers.get(XFCC_HEADER_KEY);

        if (clientCert != null && !clientCert.isEmpty()) {
            try {
                Map<String, String> certInfo = XfccParser.parseClientCert(clientCert);
                User user = new IstioUser(certInfo.get("URI"), certInfo);
                SecurityContextHolder.setSecurityContext(new SecurityContext(user));
            } catch (Exception e) {
                logger.warn("Failed to parse Istio client certificate: " + e.getMessage());
                throw new AuthenticationException(e);
            }
        }
    }

    @Override
    public void doHandleException(Exception e) {
        logger.error("Istio Authentication error: ", e);
    }


    @Override
    public GrpcFilter<Req, Res> getInstance() {
        return new GrpcIstioCertAuthenticationFilter<>();
    }
}
