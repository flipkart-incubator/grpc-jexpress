package com.flipkart.grpc.jexpress.module;

import com.codahale.metrics.health.HealthCheck;
import com.flipkart.gjex.core.filter.Filter;
import com.flipkart.gjex.core.tracing.TracingSampler;
import com.flipkart.grpc.jexpress.filter.CreateLoggingFilter;
import com.flipkart.grpc.jexpress.filter.GetLoggingFilter;
import com.flipkart.grpc.jexpress.healthcheck.AllIsWellHealthCheck;
import com.flipkart.grpc.jexpress.service.SampleService;
import com.flipkart.grpc.jexpress.tracing.AllWhitelistTracingSampler;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.grpc.BindableService;

public class SampleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BindableService.class).annotatedWith(Names.named("SampleService")).to(SampleService.class);
        bind(HealthCheck.class).to(AllIsWellHealthCheck.class);
        bind(Filter.class).annotatedWith(Names.named("GetLoggingFilter")).to(GetLoggingFilter.class);
        bind(Filter.class).annotatedWith(Names.named("CreateLoggingFilter")).to(CreateLoggingFilter.class);
        bind(TracingSampler.class).to(AllWhitelistTracingSampler.class);
    }
}
