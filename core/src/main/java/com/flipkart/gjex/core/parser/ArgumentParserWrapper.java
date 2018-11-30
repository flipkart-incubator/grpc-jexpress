package com.flipkart.gjex.core.parser;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

public class ArgumentParserWrapper {
    private ArgumentParser parser;

    public ArgumentParserWrapper() {
        this.parser = buildParser();
    }

    public Namespace parseArguments(String... arguments) throws ArgumentParserException {
        return parser.parseArgs(arguments);
    }

    private ArgumentParser buildParser() {
        ArgumentParser parser = ArgumentParsers.newFor("server").build()
                .defaultHelp(true)
                .description("");
        final Subparser subparser = parser.addSubparsers().addParser("server", false);
        subparser.addArgument("file")
                .nargs("?")
                .help("application configuration file");

        return parser;
    }
}
