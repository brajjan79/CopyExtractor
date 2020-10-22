package com.github.extractor.configuration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.extractor.exceptions.HelpGivenException;
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
     * @throws HelpGivenException
     */
    public static JsonObject parseArgs(final String[] args) throws HelpGivenException {
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

    private static CommandLine parseInputToCommandLine(final String[] args, final Options options) throws HelpGivenException {
        checkForHelpOption(args, options);
        try {
            final CommandLineParser parser = new DefaultParser();
            final CommandLine commandLine = parser.parse(options, args);
            return commandLine;
        } catch (final ParseException e) {
            System.out.println(e.getMessage());
            writeHelp(options);
            throw new InputException("Missing required options!");
        }
    }

    private static void checkForHelpOption(final String[] args, final Options options) throws HelpGivenException {
        for (final String argument : args) {
            final Boolean containsHelp = argument.equals("--help") || argument.equals("-h");
            if (containsHelp) {
                writeHelp(options);
                throw new HelpGivenException("Help option was given.");
            }
        }
    }

    private static void writeHelp(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        final String usageExample = String.format("java --jar %s %s [options value]", JAR_FILE_NAME,
                                                  "");
        formatter.printHelp(100, usageExample, "\nOptions:\n", options, null);
    }

}
