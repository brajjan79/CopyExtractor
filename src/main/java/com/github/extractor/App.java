package com.github.extractor;

import com.github.extractor.configuration.Cli;
import com.github.extractor.configuration.ConfigFactory;
import com.github.extractor.exceptions.ArgsExitGivenException;
import com.google.gson.JsonObject;

public class App {
    public static void main(final String[] args) {
        try {
            final JsonObject cliOptions = Cli.parseArgs(args);
            ConfigFactory.createFromInputArgs(cliOptions);
            new Executor().run();
        } catch (final ArgsExitGivenException e) {
            System.exit(0);
        }
    }
}
