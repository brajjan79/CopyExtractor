package com.github.extractor.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.extractor.models.ConfigFolder;

public class ConfigurationTest {

    private static final String DEFAULT_GROUP_BY_REGEX = "(?!x)x";
    private ConfigFolder folder;
    private List<ConfigFolder> folders;

    @BeforeEach
    public void beforeTests() {
        folders = new ArrayList<>();
        folder = new ConfigFolder("/input", "/output");
        folders.add(folder);
    }

    @Test
    public void testDefaults() throws Throwable {
        final Configuration config = new Configuration(null, null, null, folders, null, false, false, false);

        assertTrue(config.getFileTypes().isEmpty());
        assertTrue(config.getIgnored().isEmpty());
        assertTrue(config.getIncludeFolders().isEmpty());

        assertEquals(folder.toString(), config.getFolders().get(0).toString());
        assertEquals(DEFAULT_GROUP_BY_REGEX, config.getGroupByRegex());

        assertFalse(config.isKeepFolder());
        assertFalse(config.isKeepFolderStructure());
        assertFalse(config.isRecursive());
        assertFalse(config.isRecursive());
        assertFalse(config.isDryRun());
    }

    @Test
    public void testProvidedLists() throws Throwable {
        final List<String> ignores = new ArrayList<>();
        ignores.add("ignore");
        final List<String> files = new ArrayList<>();
        files.add("jpg");
        final List<String> includes = new ArrayList<>();
        includes.add("info");
        final Configuration config = new Configuration(files, ignores, includes, folders, null, false, false, false);

        assertEquals("jpg", config.getFileTypes().get(0));
        assertEquals("ignore", config.getIgnored().get(0));
        assertEquals("info", config.getIncludeFolders().get(0));
    }

    @Test
    public void testToString() throws Throwable {
        final String expectedString = "Configuration:\n" +
                "folders: [{inputFolder: " +
                new File("/input").getPath() +
                ", outputFolder: " +
                new File("/output").getPath() +
                "}]\n" +
                "fileEndingsToCopy: [jpg]\n" +
                "includedeFolders: [info]\n" +
                "ignoreList: [ignore]\n" +
                "groupBy: 'regex'\n" +
                "keepFolder: true\n" +
                "keepFolderStructure: true\n" +
                "recursive: true\n" +
                "dryRun: true";

        final List<String> ignores = new ArrayList<>();
        ignores.add("ignore");
        final List<String> files = new ArrayList<>();
        files.add("jpg");
        final List<String> includes = new ArrayList<>();
        includes.add("info");
        final Configuration config = new Configuration(files, ignores, includes, folders, "regex", true, true, true);
        config.setDryRun(true);

        assertEquals(expectedString, config.toString());
    }

    @Test
    public void testNoFoldersThrowsException() throws Throwable {
        final Configuration config = new Configuration(null, null, null, null, null, false, false, false);
        assertThrows(RuntimeException.class, () -> {
            config.getFolders();
        });
    }
}
