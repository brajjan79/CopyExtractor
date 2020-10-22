package com.github.extractor.configuration.models;

import static org.junit.Assert.assertEquals;

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
        assertEquals("/some/path", configFolder.getInputFolder());
        assertEquals("/some/other/path", configFolder.getOutputFolder());
    }

    @Test
    public void testConfigToString() {
        final ConfigFolder configFolder = new ConfigFolder("/some/path", "/some/other/path");
        assertEquals("{inputFolder: /some/path, outputFolder: /some/other/path}", configFolder.toString());
    }
}
