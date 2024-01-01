package com.github.extractor.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.extractor.configuration.Configuration;

public class FileHandlerTest {

    @Mock
    private Configuration config;

    private FileHandler fileHandler;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        final List<String> copyFiles = new ArrayList<>(Arrays.asList("included"));
        final List<String> ignoderFiles = new ArrayList<>(Arrays.asList("ignored"));
        when(config.getIgnored()).thenReturn(ignoderFiles);
        when(config.getFileTypes()).thenReturn(copyFiles);

        fileHandler = new FileHandler(config);
    }

    @Test
    public void testFileEndingIncluded() throws Throwable {
        final File file = new File("src/test/resources/folder_structure/folder_with_file/picture.included");
        final boolean result = fileHandler.isIncludedFileType(file);
        assertTrue("Expected file " + file.getName() + " to end with .png", result);
    }

    @Test
    public void testFileEndingNotIncluded() throws Throwable {
        final File file = new File("src/test/resources/folder_structure/folder_with_file/picture.not");
        final boolean result = fileHandler.isIncludedFileType(file);
        assertFalse("Expected file " + file.getName() + " ending with .png to be excluded.",
                result);
    }

    @Test
    public void testFileToBeIgnored() throws Throwable {
        final File file = new File("src/test/resources/folder_structure/folder_with_file/ignored.png");
        final boolean result = fileHandler.isIgnored(file);
        assertTrue("Expected file " + file.getName() + " to be ignored.", result);
    }

    @Test
    public void testFileNotToBeIgnored() throws Throwable {
        final File file = new File("src/test/resources/folder_structure/folder_with_file/isOk.png");
        final boolean result = fileHandler.isIgnored(file);
        assertFalse("Expected file " + file.getName() + " ending with .png to be excluded.",
                result);
    }

}
