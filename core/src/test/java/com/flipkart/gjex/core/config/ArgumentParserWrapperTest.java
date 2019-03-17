package com.flipkart.gjex.core.config;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgumentParserWrapperTest {

    private ArgumentParserWrapper parser;

    @Before
    public void setUp() {
        parser = new ArgumentParserWrapper();
    }

    @Test
    public void getYmlFilePath() throws ArgumentParserException {
        String[] arguments = {"server", "/a/b/config.yml"};
        Namespace namespace = parser.parseArguments(arguments);
        assertThat(namespace.get("file").equals("/a/b/config.yml"));
    }

    @Test
    public void getConfigServiceUrlYmlFilePath() throws ArgumentParserException {
        String[] arguments = {"server", "config-svc://10.47.0.101:80/bucket-name"};
        Namespace namespace = parser.parseArguments(arguments);
        assertThat(namespace.get("file").equals("config-svc://10.47.0.101:80/bucket-name"));
    }
}