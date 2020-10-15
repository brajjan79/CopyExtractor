package com.github.extractor.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.extractor.configuration.models.ConfigFolder;

public class ConfigurationTest {

    private static final String DEFAULT_GROUP_BY_REGEX = "(?!x)x";
    private ConfigFolder folder;
    private List<ConfigFolder> folders;

    @Before
    public void beforeTests() {
        folders = new ArrayList<>();
        folder = new ConfigFolder("/input", "/output");
        folders.add(folder);
    }

    @Test
    public void testDefaults() throws Throwable {
        final Configuration config = new Configuration(null, null, null, folders, null, false, false, false);

        assertTrue("Default fileEndings should be empty.", config.getFileTypes().isEmpty());
        assertTrue("Default ignoreList should be empty.", config.getIgnored().isEmpty());
        assertTrue("Default includedDirs should be empty.", config.getIncludeFolders().isEmpty());

        assertEquals(folder.toString(), config.getFolders().get(0).toString());
        assertEquals(DEFAULT_GROUP_BY_REGEX, config.getGroupByRegex());

        assertFalse("Default keepFolder should be true.", config.isKeepFolder());
        assertFalse("Default keepFolderStructure should be true.", config.isKeepFolderStructure());
        assertFalse("Default recursive should be true.", config.isRecursive());
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


    @Test(expected=RuntimeException.class)
    public void testNoFoldersThrowsException() throws Throwable {
        final Configuration config = new Configuration(null, null, null, null, null, false, false, false);
        config.getFolders();
    }
}
