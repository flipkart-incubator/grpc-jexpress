package com.flipkart.grpc.jexpress;

import com.flipkart.gjex.core.Application;
import com.flipkart.gjex.core.setup.Bootstrap;
import com.flipkart.gjex.core.setup.Environment;
import com.flipkart.gjex.guice.GuiceBundle;
import com.flipkart.grpc.jexpress.module.SampleModule;

import java.util.Map;

public class SampleApplication extends Application<SampleConfiguration, Map> {

    @Override
    public String getName() {
        return "Sample JExpress Application";
    }

    @Override
    public void run(SampleConfiguration configuration, Map configMap, Environment environment) throws Exception {

    }

    @Override
    public void initialize(Bootstrap<SampleConfiguration, Map> bootstrap) {
        GuiceBundle<SampleConfiguration, Map> guiceBundle = new GuiceBundle.Builder<SampleConfiguration, Map>()
                .setConfigClass(SampleConfiguration.class)
                .addModules(new SampleModule())
                .build();
        bootstrap.addBundle(guiceBundle);
    }

    public static void main(String [] args) throws Exception {
        SampleApplication app = new SampleApplication();
        app.run(args);
    }
}
