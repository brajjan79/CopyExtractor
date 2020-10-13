package com.github.extractor.configuration;

import com.github.extractor.exceptions.ConfigurationException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConfigFactory {

    public static Configuration createFromInputArgs(final JsonObject cliOptions) {
        if (cliOptions.has("config-file-path")) {
            return createFromConfigFilePath(cliOptions.get("config-file-path").getAsString());
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
