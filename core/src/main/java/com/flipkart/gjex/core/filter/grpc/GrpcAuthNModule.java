package com.flipkart.gjex.core.filter.grpc;

import com.flipkart.gjex.core.filter.grpc.authFilter.GrpcAuthenticationExceptionHandlingFilter;
import com.flipkart.gjex.core.filter.grpc.authFilter.GrpcEnsureAuthenticationFilter;
import com.flipkart.gjex.core.filter.grpc.authFilter.GrpcIstioCertAuthenticationFilter;
import com.flipkart.gjex.core.filter.grpc.authFilter.GrpcSecurityContextPersistenceFilter;
import com.flipkart.gjex.core.filter.grpc.authFilter.GrpcSystemUserAuthenticationFilter;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GrpcAuthNModule extends AbstractModule {

    private static final Logger logger = Logger.getLogger(GrpcAuthNModule.class.getName());
    private final AuthConfig authConfig;
    public static List<Class<? extends GrpcFilter>> authnFilters;

    public GrpcAuthNModule(AuthConfig authConfig) {
        this.authConfig = authConfig;
        authnFilters = new ArrayList<>();
    }

    @Override
    public void configure() {
        bind(GrpcFilter.class).annotatedWith(Names.named("GrpcAuthenticationExceptionHandlingFilter")).to(GrpcAuthenticationExceptionHandlingFilter.class);
        authnFilters.add(GrpcAuthenticationExceptionHandlingFilter.class);

        bind(GrpcFilter.class).annotatedWith(Names.named("GrpcSecurityContextPersistenceFilter")).to(GrpcSecurityContextPersistenceFilter.class);
        authnFilters.add(GrpcSecurityContextPersistenceFilter.class);

        GrpcSystemUserAuthenticationFilter grpcSystemUserAuthenticationFilter = new GrpcSystemUserAuthenticationFilter(authConfig.getMultiAuthnUrls(), authConfig.getWhiteListedClientIds(), authConfig.getClientId(), authConfig.getAuthIgnoreUrls());
        bind(GrpcFilter.class).annotatedWith(Names.named("GrpcSystemUserAuthenticationFilter")).toInstance(grpcSystemUserAuthenticationFilter);
        authnFilters.add(GrpcSystemUserAuthenticationFilter.class);

        if (authConfig.isEnableIstio()) {
            bind(GrpcFilter.class).annotatedWith(Names.named("GrpcIstioCertAuthenticationFilter")).to(GrpcIstioCertAuthenticationFilter.class);
            authnFilters.add(GrpcIstioCertAuthenticationFilter.class);
        }

        GrpcEnsureAuthenticationFilter grpcEnsureAuthenticationFilter = new GrpcEnsureAuthenticationFilter(authConfig.getLoginUrl(), authConfig.getAuthIgnoreUrls(), authConfig.isSaveRequest());
        bind(GrpcFilter.class).annotatedWith(Names.named("GrpcEnsureAuthenticationFilter")).toInstance(grpcEnsureAuthenticationFilter);
        authnFilters.add(GrpcEnsureAuthenticationFilter.class);
    }
}
