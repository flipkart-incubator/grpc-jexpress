package com.flipkart.gjex.guice.module;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.inject.Inject;
import javax.inject.Named;

@Named("StaticAssetsRegistrar")
public class StaticAssetsRegistrar {

    private final ServletContextHandler context;

    @Inject
    public StaticAssetsRegistrar(@Named("ApiServletContext") ServletContextHandler context) {
        this.context = context;
        registerStaticAssets();
    }

    public void registerStaticAssets() {
        // static resources are in the classpath under /console/
        String resourceBase = this.getClass().getResource("/console/").toExternalForm();

        ServletHolder staticServletHolder = new ServletHolder("static", DefaultServlet.class);
        staticServletHolder.setInitParameter("resourceBase", resourceBase);
        staticServletHolder.setInitParameter("dirAllowed", "false");
        staticServletHolder.setInitParameter("pathInfoOnly", "true");

        context.addServlet(staticServletHolder, "/console/*");
    }
}

