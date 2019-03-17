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
        this.typoFile = resourceFileName("factory-test-typo.yml");
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