package com.github.extractor;

import com.github.extractor.configuration.ConfigFactory;
import com.github.extractor.configuration.Configuration;
import com.github.extractor.exceptions.InputException;
import com.google.gson.JsonObject;

public class App {
	public static void main(final String[] args) {
        try {
            final JsonObject cliOptions = Cli.parseArgs(args);
            final Configuration config = ConfigFactory.createFromInputArgs(cliOptions);
            new Executor(config).run();
        } catch (final InputException e) {
            System.exit(1);
        } catch (final Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
	}
}
