package com.github.extractor.configuration;

import javax.naming.ConfigurationException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ConfigFactory {

    public static Configuration createFromInputArgs(final JsonObject cliOptions) throws ConfigurationException {
        if (cliOptions.has("config-file-path")) {
            return createFromConfigFilePath(cliOptions.get("config-file-path").getAsString());
        }
        throw new ConfigurationException("Missing provided configuration.");
    }

    public static Configuration createFromConfigFilePath(final String configFilePath) {
        final JsonObject jsonConfig = ConfigFile.readConfigurationFile(configFilePath);
        ConfigurationValidator.validate(jsonConfig);
        return createConfiguration(jsonConfig);
    }

    private static Configuration createConfiguration(final JsonObject jsonConfig) {
        final Gson gson = new Gson();
        final Configuration config = gson.fromJson(jsonConfig, Configuration.class);
        return config;
    }
}
