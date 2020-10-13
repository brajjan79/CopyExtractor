package com.github.extractor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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
     */
    public static JsonObject parseArgs(final String[] args) {
        final Options options = addOptions();
        final CommandLine commandLine = parseInputToCommandLine(args, options);
        final JsonObject inputConfig = parseCommandLineInputToJson(commandLine);
        return inputConfig;
    }

    private static JsonObject parseCommandLineInputToJson(final CommandLine commandLine) {
        final JsonObject inputConfig = new JsonObject();

        for (final Option option : commandLine.getOptions()) {
            final String key = insertKeyValuedToJsonStructure(option);
            final String value = option.getValue();
            inputConfig.addProperty(key, value);
        }
        return inputConfig;
    }

    private static String insertKeyValuedToJsonStructure(final Option option) {
        String key = option.getOpt();
        if (option.hasLongOpt()) {
            key = option.getLongOpt();
        }
        return key;
    }

    private static Options addOptions() {
        final Options options = new Options();
        final Option configFilePathOption = new Option("f", "config-file-path", VALUE_REQUIRED, "Local path to configuration file.");
        configFilePathOption.setRequired(false);
        configFilePathOption.setArgName("Config");
        options.addOption(configFilePathOption);

        final Option sourceFolderOption = new Option("s", "source-folder", VALUE_REQUIRED, "Folder to extract from.");
        sourceFolderOption.setRequired(false);
        sourceFolderOption.setArgName("Source");
        options.addOption(sourceFolderOption);

        final Option targetFolderOption = new Option("t", "target-folder", VALUE_REQUIRED, "Folder to extract to.");
        targetFolderOption.setRequired(false);
        targetFolderOption.setArgName("Target");
        options.addOption(targetFolderOption);

        final Option recursiceOption = new Option("R", "recursive", VALUE_NOT_REQUIRED, "Extract recursively.");
        recursiceOption.setRequired(false);
        recursiceOption.setArgName("Recursive");
        options.addOption(recursiceOption);

        final Option keepFolderStructureOption = new Option("k", "keep-folder-structure", VALUE_NOT_REQUIRED, "Target dirs will keep the same folder structure as source.");
        keepFolderStructureOption.setRequired(false);
        keepFolderStructureOption.setArgName("Keep Structure");
        options.addOption(keepFolderStructureOption);

        final Option helpOption = new Option("h", "help", VALUE_NOT_REQUIRED,
                                             "Prints this Help Text");
        helpOption.setRequired(false);
        options.addOption(helpOption);

        return options;
    }

    private static CommandLine parseInputToCommandLine(final String[] args, final Options options) {
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

    private static void checkForHelpOption(final String[] args, final Options options) {
        for (final String argument : args) {
            final Boolean containsHelp = argument.equals("--help") || argument.equals("-h");
            if (containsHelp) {
                writeHelp(options);
                throw new InputException("Help option was given.");
            }
        }
    }

    private static void writeHelp(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        final String usageExample = String.format("java --jar %s %s [options value]", JAR_FILE_NAME,
                                                  "");
        formatter.printHelp(100, usageExample, "\nOptions:\n", options, null);
    }

}
