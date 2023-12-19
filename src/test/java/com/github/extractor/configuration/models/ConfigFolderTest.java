package com.github.extractor.configuration.models;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.github.extractor.exceptions.ConfigurationException;

public class ConfigFolderTest {

    @Test(expected=ConfigurationException.class)
    public void testConfigFolderNoInputFolder() {
        final ConfigFolder configFolder = new ConfigFolder(null, null);
        configFolder.getInputFolder();
    }

    @Test(expected=ConfigurationException.class)
    public void testConfigFolderNoOutPutFolder() {
        final ConfigFolder configFolder = new ConfigFolder(null, null);
        configFolder.getOutputFolder();
    }

    @Test
    public void testConfig() {
        final ConfigFolder configFolder = new ConfigFolder("/some/path", "/some/other/path");
        assertEquals(new File("/some/path").getPath(), configFolder.getInputFolder().getPath());
        assertEquals(new File("/some/other/path").getPath(), configFolder.getOutputFolder().getPath());
    }

    @Test
    public void testConfigToString() {
        final ConfigFolder configFolder = new ConfigFolder("/some/path", "/some/other/path");
        assertEquals(
                "{inputFolder: " + new File("/some/path").getPath() + ", outputFolder: " + new File("/some/other/path").getPath() + "}",
                configFolder.toString());
    }
}
