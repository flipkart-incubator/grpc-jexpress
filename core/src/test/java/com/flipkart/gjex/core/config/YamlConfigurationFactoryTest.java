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

import com.flipkart.gjex.core.GJEXObjectMapper;
import org.junit.Before;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class YamlConfigurationFactoryTest  extends BaseConfigurationFactoryTest {

    @Before
    public void setUp() throws Exception {
        this.factory = new YamlConfigurationFactory<>(ExampleConfig.class, validator, GJEXObjectMapper.newObjectMapper());
        this.malformedFile = resourceFileName("factory-test-malformed.yml");
        this.emptyFile = resourceFileName("factory-test-empty.yml");
        this.validFile = resourceFileName("factory-test-valid.yml");
        this.wrongTypeFile = resourceFileName("factory-test-wrong-type.yml");
        this.malformedAdvancedFile = resourceFileName("factory-test-malformed-adv.yml");
    }

    @Override
    public void throwsAnExceptionOnMalformedFiles() {
        assertThatThrownBy(super::throwsAnExceptionOnMalformedFiles)
                .hasMessageContaining(" * Failed to parse configuration; Cannot construct instance of `com.flipkart.gjex.core.config.BaseConfigurationFactoryTest$ExampleConfig`");
    }

    @Override
    public void printsDetailedInformationOnMalformedContent() throws Exception {
        assertThatThrownBy(super::printsDetailedInformationOnMalformedContent)
                .hasMessageContaining(String.format(
                        "%s has an error:%n" +
                                "  * Malformed YAML at line: 19, column: 28; while parsing a flow sequence\n" +
                                " in 'reader'", malformedAdvancedFile.getAbsolutePath()));
    }

}
