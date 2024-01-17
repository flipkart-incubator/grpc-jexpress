package com.flipkart.gjex.core.web.p8s;

import io.prometheus.metrics.config.PrometheusProperties;
import io.prometheus.metrics.exporter.common.PrometheusScrapeHandler;
import io.prometheus.metrics.model.registry.PrometheusRegistry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * PrometheusMetricsServlet for javax servlets.
 * Once upgraded to jakarta servlets, this can be removed and replaced with the one from io.prometheus.metrics.exporter.servlet.jakarta
 * @author kingster
 *
 */
public class PrometheusMetricsServlet extends HttpServlet {

    private final PrometheusScrapeHandler handler;

    public PrometheusMetricsServlet() {
        this(PrometheusProperties.get(), PrometheusRegistry.defaultRegistry);
    }

    public PrometheusMetricsServlet(PrometheusRegistry registry) {
        this(PrometheusProperties.get(), registry);
    }

    public PrometheusMetricsServlet(PrometheusProperties config) {
        this(config, PrometheusRegistry.defaultRegistry);
    }

    public PrometheusMetricsServlet(PrometheusProperties config, PrometheusRegistry registry) {
        this.handler = new PrometheusScrapeHandler(config, registry);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        handler.handleRequest(new HttpExchangeAdapter(request, response));
    }
}


