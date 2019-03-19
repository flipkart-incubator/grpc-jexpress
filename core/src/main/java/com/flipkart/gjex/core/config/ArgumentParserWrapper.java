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

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public class ArgumentParserWrapper {

    private final ArgumentParser parser;

    public ArgumentParserWrapper() {
        this.parser = buildParser();
    }

    public Namespace parseArguments(String... arguments) throws ArgumentParserException {
        return parser.parseArgs(arguments);
    }

    private ArgumentParser buildParser() {
        ArgumentParser parser = ArgumentParsers.newFor("server").build()
                .defaultHelp(true)
                .description("Runs grpc server");
        final Subparser subparser = parser.addSubparsers().addParser("server", false);
        subparser.addArgument("file")
                .nargs("?")
                .help("Application configuration file");
        return parser;
    }
}
