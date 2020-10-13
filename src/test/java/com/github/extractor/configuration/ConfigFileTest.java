package com.github.extractor.configuration;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.WriteAbortedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConfigFileUtil.class, FileWriter.class })
public class ConfigFileTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File configFilePath;
    private Gson gson;

    private Configuration configuration;

    @Before
    public void init() throws Throwable {
        configFilePath = folder.newFile("config_file_for_test.json");
        gson = new GsonBuilder().setPrettyPrinting().create();
        configuration = new Configuration(null, null, null, null, null, false, false, false);
    }

    @Test
    public void testConstructor() throws Throwable {
        new ConfigFileUtil();
    }

    @Test
    public void testWriteThenReadConfiguration() throws Throwable {
        ConfigFileUtil.saveConfigurationFile(configuration, configFilePath.getAbsolutePath());

        final JsonObject configFileJson = ConfigFileUtil.readConfigurationFile(configFilePath.getAbsolutePath());

        final JsonObject expectedJson = (JsonObject) gson.toJsonTree(configuration);
        assertTrue("Loaded configuration file should be the same as saved config file.",
            configFileJson.equals(expectedJson));
    }

    @Test(expected = WriteAbortedException.class)
    public void testWriteThrowsJsonIOException() throws Throwable {
        final FileWriter mockedFileWriter = mock(FileWriter.class);
        whenNew(FileWriter.class).withAnyArguments().thenReturn(mockedFileWriter);
        doThrow(new JsonIOException("")).when(mockedFileWriter, "write", Mockito.any());
        ConfigFileUtil.saveConfigurationFile(configuration, configFilePath.getAbsolutePath());
    }

    @Test(expected = WriteAbortedException.class)
    public void testWriteThrowsIOException() throws Throwable {
        final FileWriter mockedFileWriter = mock(FileWriter.class);
        whenNew(FileWriter.class).withAnyArguments().thenReturn(mockedFileWriter);
        doThrow(new IOException("")).when(mockedFileWriter, "write", Mockito.any());
        ConfigFileUtil.saveConfigurationFile(configuration, configFilePath.getAbsolutePath());
    }

    @Test(expected = JsonParseException.class)
    public void testReadThrowsIOException() throws Throwable {
        ConfigFileUtil.readConfigurationFile(configFilePath.getAbsolutePath() + "/invalid/");
    }

    @Test(expected = JsonParseException.class)
    public void testReadThrowsJsonSyntaxException() throws Throwable {
        ConfigFileUtil.saveConfigurationFile(configuration, configFilePath.getAbsolutePath());
        whenNew(String.class).withAnyArguments().thenReturn("Some invalid json ' Yes ' in JSON!");
        ConfigFileUtil.readConfigurationFile(configFilePath.getAbsolutePath());
    }
}
