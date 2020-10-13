package com.github.extractor.configuration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.WriteAbortedException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ConfigFileUtil {

    public static JsonObject readConfigurationFile(final String pathString) {
        final Path path = Paths.get(pathString);
        return readConfigurationFile(path);
    }

    public static JsonObject readConfigurationFile(final Path path) {
        try {
            final String fileString = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            final JsonObject jsonObject = JsonParser.parseString(fileString).getAsJsonObject();
            return jsonObject;
        } catch (IOException | JsonSyntaxException e) {
            throw new JsonParseException("Could not read Json configuration file.", e);
        }
    }

    public static void saveConfigurationFile(final Configuration config, final String filePath)
            throws WriteAbortedException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            final String jsonString = gson.toJson(config);
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(jsonString);
            }
        } catch (JsonIOException | IOException e) {
            throw new WriteAbortedException("Failed to save data to configuration file", e);
        }
    }
}
