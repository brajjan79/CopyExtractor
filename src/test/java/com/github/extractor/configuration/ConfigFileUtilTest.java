package com.github.extractor.configuration;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.WriteAbortedException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.github.extractor.utils.FileTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class ConfigFileUtilTest {

    @TempDir
    public File folder;

    private File configFilePath;
    private Gson gson;

    private Configuration configuration;

    @BeforeEach
    public void init() throws Throwable {
        configFilePath = new File(folder, "config_file_for_test.json");
        gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(File.class, new FileTypeAdapter()).create();
        configuration = new Configuration(null, null, null, null, null, false, false, false, false);
    }

    @Test
    public void testConstructor() throws Throwable {
        try {
            new ConfigFileUtil();
        } catch (final Exception e) {
            fail("Failed to initiate ConfigFactory");
        }
    }

    @Test
    public void testWriteThenReadConfiguration() throws Throwable {
        ConfigFileUtil.saveConfigurationFile(configuration, configFilePath.getAbsolutePath());

        final JsonObject configFileJson = ConfigFileUtil.readConfigurationFile(configFilePath.getAbsolutePath());

        final JsonObject expectedJson = (JsonObject) gson.toJsonTree(configuration);
        assertTrue("Loaded configuration file should be the same as saved config file.",
                configFileJson.equals(expectedJson));
    }

    @Test
    public void testWriteThrowsJsonIOException() throws Throwable {
        try (MockedConstruction<FileWriter> mockExecutor = Mockito.mockConstruction(FileWriter.class,
                (mock, context) -> {
                    doThrow(new JsonIOException("")).when(mock).write(Mockito.any(String.class));
                })) {
            assertThrows(WriteAbortedException.class, () -> {
                ConfigFileUtil.saveConfigurationFile(configuration, configFilePath.getAbsolutePath());
            });
        }
    }

    @Test
    public void testWriteThrowsIOException() throws Throwable {
        try (MockedConstruction<FileWriter> mockExecutor = Mockito.mockConstruction(FileWriter.class,
                (mock, context) -> {
                    doThrow(new IOException("")).when(mock).write(Mockito.any(String.class));
                })) {
            assertThrows(WriteAbortedException.class, () -> {
                ConfigFileUtil.saveConfigurationFile(configuration, configFilePath.getAbsolutePath());
            });
        }
    }

    @Test
    public void testReadThrowsIOException() throws Throwable {
        try (MockedStatic<Files> mocked = Mockito.mockStatic(Files.class)) {
            mocked.when(() -> Files.readAllBytes(Mockito.any())).thenThrow(new IOException(""));
            assertThrows(JsonParseException.class, () -> {
                ConfigFileUtil.readConfigurationFile(configFilePath.getAbsolutePath());
            });
        }
    }

    @Test
    public void testReadThrowsJsonSyntaxException() throws Throwable {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class);
                MockedStatic<JsonParser> mockedJsonParser = Mockito.mockStatic(JsonParser.class)) {
            mockedFiles.when(() -> Files.readAllBytes(Mockito.any())).thenReturn("Some invalid json ' Yes ' in JSON!".getBytes());
            mockedJsonParser.when(() -> JsonParser.parseString(Mockito.any())).thenThrow(new JsonSyntaxException(""));
            assertThrows(JsonParseException.class, () -> {
                ConfigFileUtil.readConfigurationFile(configFilePath.getAbsolutePath());
            });
        }
    }
}
