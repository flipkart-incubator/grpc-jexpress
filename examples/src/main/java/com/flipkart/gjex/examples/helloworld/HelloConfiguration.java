package com.flipkart.gjex.examples.helloworld;

import com.flipkart.gjex.core.config.JExpressConfiguration;

public class HelloConfiguration extends JExpressConfiguration {
    private String guiceConfigFile;

    public String getGuiceConfigFile() {
        return guiceConfigFile;
    }

    public void setGuiceConfigFile(String guiceConfigFile) {
        this.guiceConfigFile = guiceConfigFile;
    }
}
