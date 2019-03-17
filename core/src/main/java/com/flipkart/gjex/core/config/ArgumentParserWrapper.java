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
