package com.github.extractor.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.extractor.configuration.Configuration;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "com.github.extractor.handlers.*")
public class FileHandlerTest {

    private Configuration config;
    private FileHandler fileHandler;

    @Before
    public void init() {
        config = PowerMockito.mock(Configuration.class);
        fileHandler = new FileHandler(config);
    }

    @Test
    public void testFileEndingIncluded() throws Throwable {
        final List<String> copyFiles = new ArrayList<>(Arrays.asList("png"));
        when(config.getCopyFiles()).thenReturn(copyFiles);
        final File file = new File("src/test/resources/folder_structure/folder_with_file/picture.png");
        final boolean result = fileHandler.isIncludedFileType(file);
        assertTrue("Expected file " + file.getName() + " to end with .png", result);
    }

    @Test
    public void testFileEndingNotIncluded() throws Throwable {
        final File file = new File("src/test/resources/folder_structure/folder_with_file/picture.png");
        final boolean result = fileHandler.isIncludedFileType(file);
        assertFalse("Expected file " + file.getName() + " ending with .png to be excluded.",
                    result);
    }

    @Test
    public void testFileToBeIgnored() throws Throwable {
        final List<String> ignoderFiles = new ArrayList<>(Arrays.asList("picture"));
        when(config.getIgnoredFolders()).thenReturn(ignoderFiles);
        final File file = new File("src/test/resources/folder_structure/folder_with_file/picture.png");
        final boolean result = fileHandler.isIgnored(file);
        assertTrue("Expected file " + file.getName() + " to be ignored.", result);
    }

    @Test
    public void testFileNotToBeIgnored() throws Throwable {
        final File file = new File("src/test/resources/folder_structure/folder_with_file/picture.png");
        final boolean result = fileHandler.isIgnored(file);
        assertFalse("Expected file " + file.getName() + " ending with .png to be excluded.",
                    result);
    }

}
