package com.flipkart.gjex.core.config;

import com.flipkart.gjex.core.parser.ArgumentParserWrapper;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class NamespaceTest {
    ArgumentParserWrapper parser;

    @Before
    public void setup() {
        parser = new ArgumentParserWrapper();
    }

    @Test
    public void namespaceTest() throws ArgumentParserException {
        String[] arguments = {"server", "file-name.txt1"};
        Namespace namespace = parser.parseArguments(arguments);
        Assert.assertTrue(namespace.get("file").equals("file-name.txt"));
    }
}
