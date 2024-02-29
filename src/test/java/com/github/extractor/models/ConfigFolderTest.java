package com.github.extractor.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.github.extractor.exceptions.ConfigurationException;
import com.github.extractor.exceptions.FolderException;

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
    public void testConfigValidate() throws Exception {
        final File mockedInputFolder = mock(File.class);
        final File mockedOutputFolder = mock(File.class);
        when(mockedInputFolder.isFile()).thenReturn(false);
        when(mockedInputFolder.exists()).thenReturn(true);
        final ConfigFolder configFolder = new ConfigFolder(mockedInputFolder, mockedOutputFolder);
        assertDoesNotThrow(() -> {
            configFolder.validate();
        });
    }

    @Test
    public void testConfigValidateFolderIsFile() throws Exception {
        final File mockedInputFolder = mock(File.class);
        final File mockedOutputFolder = mock(File.class);
        when(mockedInputFolder.isFile()).thenReturn(true);
        when(mockedInputFolder.exists()).thenReturn(true);
        final ConfigFolder configFolder = new ConfigFolder(mockedInputFolder, mockedOutputFolder);
        assertThrows(FolderException.class, () -> {
            configFolder.validate();
        });
    }

    @Test
    public void testConfigValidateFolderNotExists() throws Exception {
        final File mockedInputFolder = mock(File.class);
        final File mockedOutputFolder = mock(File.class);
        when(mockedInputFolder.isFile()).thenReturn(false);
        when(mockedInputFolder.exists()).thenReturn(false);
        final ConfigFolder configFolder = new ConfigFolder(mockedInputFolder, mockedOutputFolder);
        assertThrows(FolderException.class, () -> {
            configFolder.validate();
        });
    }

    @Test
    public void testConfigToString() {
        final ConfigFolder configFolder = new ConfigFolder("/some/path", "/some/other/path");
        assertEquals(
                "{inputFolder: " + new File("/some/path").getPath() + ", outputFolder: " + new File("/some/other/path").getPath() + "}",
                configFolder.toString());
    }
}
