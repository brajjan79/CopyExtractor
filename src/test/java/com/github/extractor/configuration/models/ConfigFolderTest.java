package com.github.extractor.configuration.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.github.extractor.exceptions.ConfigurationException;

public class ConfigFolderTest {

    @Test
    public void testConfigFolderNoInputFolder() {
        assertThrows(ConfigurationException.class, () -> {
            new ConfigFolder(null, "outputfolder");
        });
    }

    @Test
    public void testConfigFolderNoOutPutFolder() {
        assertThrows(ConfigurationException.class, () -> {
            new ConfigFolder("inputfolder", null);
        });
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
