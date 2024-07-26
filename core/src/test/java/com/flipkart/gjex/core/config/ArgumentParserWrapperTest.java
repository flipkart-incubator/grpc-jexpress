/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        String[] arguments = {"server", "config-svc://10.47.0.11:80/bucket-name"};
        Namespace namespace = parser.parseArguments(arguments);
        assertThat(namespace.get("file").equals("config-svc://10.47.0.11:80/bucket-name"));
    }
}
