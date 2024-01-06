package com.github.extractor.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.github.extractor.models.ConfigFolder;
import com.github.extractor.utils.FileTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class ConfigFactory {

    public static Configuration createFromInputArgs(final JsonObject cliOptions) {
        if (cliOptions.has(CliKeys.CONFIG_FILE_PATH.name)) {
            final Configuration config = createFromConfigFilePath(
                    cliOptions.get(CliKeys.CONFIG_FILE_PATH.name).getAsString());
            if (cliOptions.has(CliKeys.DRY_RUN.name)) {
                config.setDryRun(true);
            }
            return config;
        }
        final Configuration config = createFromConfigFilefromArguments(cliOptions);
        return config;
    }

    private static Configuration createFromConfigFilefromArguments(final JsonObject cliOptions) {
        final List<String> fileTypes = getElements(cliOptions, CliKeys.FILE_TYPES.name);
        final List<String> ignore = getElements(cliOptions, CliKeys.IGNORE.name);
        final List<String> includeFolders = getElements(cliOptions, CliKeys.INCLUDE_FOLDERS.name);
        final List<ConfigFolder> folders = getConfigFolder(cliOptions);

        String groupByRegex = null;
        if (cliOptions.has(CliKeys.GROUP_BY_REGEX.name)){
            groupByRegex = cliOptions.get(CliKeys.GROUP_BY_REGEX.name).getAsString();
        }

        final boolean keepFolder = cliOptions.has(CliKeys.KEEP_FOLDER.name);
        final boolean createFolder = cliOptions.has(CliKeys.CREATE_FOLDER.name);
        final boolean keepFolderStructure = cliOptions.has(CliKeys.KEEP_FOLDER_STRUCTURE.name);
        final boolean recursive = cliOptions.has(CliKeys.RECURSIVE.name);

        final Configuration config = new Configuration(
                fileTypes, ignore, includeFolders, folders, groupByRegex, keepFolder, createFolder,
                keepFolderStructure, recursive);
        config.setDryRun(cliOptions.has(CliKeys.DRY_RUN.name));
        return config;
    }

    private static List<ConfigFolder> getConfigFolder(final JsonObject cliOptions) {
        final List<ConfigFolder> folders = new ArrayList<>();
        final ConfigFolder folder = new ConfigFolder(cliOptions.get(CliKeys.SOURCE_FOLDER.name).getAsString(),
                cliOptions.get(CliKeys.TARGET_FOLDER.name).getAsString());
        folders.add(folder);
        return folders;
    }

    private static List<String> getElements(final JsonObject cliOptions, final String key) {
        final List<String> valueList = new ArrayList<>();
        if (!cliOptions.has(key)) {
            return valueList;
        }
        final String values = cliOptions.get(key).getAsString();
        final String[] listValues = values.split(",");
        for (final String value : listValues) {
            if (!value.trim().isEmpty()) {
                valueList.add(value.trim());
            }
        }
        return valueList;
    }

    private static Configuration createFromConfigFilePath(final String configFilePath) {
        final JsonObject jsonConfig = ConfigFileUtil.readConfigurationFile(configFilePath);
        return createConfiguration(jsonConfig);
    }

    private static Configuration createConfiguration(final JsonObject jsonConfig) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(File.class, new FileTypeAdapter()).create();
        final Configuration config = gson.fromJson(jsonConfig, Configuration.class);
        Configuration.setInstance(config);
        return config;
    }
}
