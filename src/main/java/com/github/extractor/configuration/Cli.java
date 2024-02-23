package com.github.extractor.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.extractor.App;
import com.github.extractor.exceptions.ArgsExitGivenException;
import com.github.extractor.exceptions.InputException;
import com.google.gson.JsonObject;

public class Cli {

    protected static final boolean VALUE_REQUIRED = true;
    protected static final boolean VALUE_NOT_REQUIRED = false;

    private static final String JAR_FILE_NAME = "FileRenamer.jar";

    /**
     *
     *
     * @param args - Program input args
     * @throws ArgsExitGivenException
     */
    public static JsonObject parseArgs(final String[] args) throws ArgsExitGivenException {
        final Options options = addOptions(isConfigFileRequired(args));
        final CommandLine commandLine = parseInputToCommandLine(args, options);
        final JsonObject inputConfig = parseCommandLineInputToJson(commandLine);
        return inputConfig;
    }

    private static boolean isConfigFileRequired(final String[] args) {
        return args.length <= 3;
    }

    private static JsonObject parseCommandLineInputToJson(final CommandLine commandLine) {
        final JsonObject inputConfig = new JsonObject();

        for (final Option option : commandLine.getOptions()) {
            final String key = option.getLongOpt();
            final String value = option.getValue();
            inputConfig.addProperty(key, value);
        }
        return inputConfig;
    }

    private static Options addOptions(final boolean isConfigFileRequired) {
        final Options options = new Options();
        for (final CliKeys key : CliKeys.values()) {
            final Option option = new Option(key.shortName, key.name, key.hasArgs, key.description);
            option.setRequired(key.isRequired(isConfigFileRequired));
            option.setArgName(key.argumentName);
            options.addOption(option);
        }

        return options;
    }

    private static CommandLine parseInputToCommandLine(final String[] args, final Options options) throws ArgsExitGivenException {
        checkForHelpOption(args, options);
        try {
            final CommandLineParser parser = new DefaultParser();
            final CommandLine commandLine = parser.parse(options, args);
            return commandLine;
        } catch (final ParseException e) {
            System.out.println(e.getMessage());
            printHelp(options);
            throw new InputException("Missing required options!");
        }
    }

    private static void checkForHelpOption(final String[] args, final Options options) throws ArgsExitGivenException {
        for (final String argument : args) {
            final Boolean containsHelp = argument.equals("--help") || argument.equals("-h");
            if (containsHelp) {
                printHelp(options);
                throw new ArgsExitGivenException("Help option was given.");
            }
            final Boolean containsVersion = argument.equals("--version") || argument.equals("-v");
            if (containsVersion) {
                printVersion();
                throw new ArgsExitGivenException("Version option was given.");
            }
            final Boolean quietMode = argument.equals("--quiet") || argument.equals("-q");
            if (quietMode) {
                setQuietMode();
            }
        }
    }

    private static void printHelp(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        final String usageExample = String.format("java --jar %s %s [options value]", JAR_FILE_NAME,
                "");
        formatter.printHelp(100, usageExample, "\nOptions:\n", options, null);
    }

    private static void printVersion() {
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

    private static void setQuietMode() {
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                // NO-OP
            }
        }));
    }
}
