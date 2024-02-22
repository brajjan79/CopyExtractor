package com.github.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.github.extractor.configuration.Cli;
import com.github.extractor.configuration.ConfigFactory;
import com.github.extractor.exceptions.HelpGivenException;
import com.google.gson.JsonObject;

public class App {
    public static void main(final String[] args) {
        try {
            if (args.length > 0 && "--version".equals(args[0])) {
                printVersion();
                System.exit(0);
            }
            final JsonObject cliOptions = Cli.parseArgs(args);
            ConfigFactory.createFromInputArgs(cliOptions);
            new Executor().run();
        } catch (final HelpGivenException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void printVersion() {
        final Properties prop = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("version.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find version.properties");
                return;
            }
            prop.load(input);
            final String version = prop.getProperty("version");
            System.out.println(version);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
