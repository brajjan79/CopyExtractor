package com.github.extractor.configuration;

import com.github.extractor.exceptions.ConfigurationException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConfigFactory {

    public static Configuration createFromInputArgs(final JsonObject cliOptions) {
        if (cliOptions.has("config-file-path")) {
            final Configuration option = createFromConfigFilePath(cliOptions.get("config-file-path").getAsString());
            if (cliOptions.has("dry-run")) {
                option.setDryRun(cliOptions.get("dry-run").getAsBoolean());
            }
            return option;
        }
        throw new ConfigurationException("Missing provided configuration.");
    }

    public static Configuration createFromConfigFilePath(final String configFilePath) {
        final JsonObject jsonConfig = ConfigFileUtil.readConfigurationFile(configFilePath);
        return createConfiguration(jsonConfig);
    }

    private static Configuration createConfiguration(final JsonObject jsonConfig) {
        final Gson gson = new Gson();
        final Configuration config = gson.fromJson(jsonConfig, Configuration.class);
        return config;
    }
}
