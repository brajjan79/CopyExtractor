package com.github.extractor.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.utils.Dirs;
import com.google.common.io.Files;

public class FileHandlerTest {

    @Mock
    private Configuration mockConfig;

    private MockedStatic<Configuration> mockConfigClass;
    private MockedStatic<Files> mockFiles;
    private MockedStatic<Dirs> mockDirs;

    private FileHandler fileHandler;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);

        mockFiles = mockStatic(Files.class);
        mockConfigClass = mockStatic(Configuration.class);
        mockDirs = mockStatic(Dirs.class);

        mockConfigClass.when(() -> Configuration.getInstance()).thenReturn(mockConfig);
        mockDirs.when(() -> Dirs.getBaseDirName(any(), any())).thenReturn("");

        final List<String> copyFiles = new ArrayList<>(Arrays.asList("included"));
        final List<String> ignoderFiles = new ArrayList<>(Arrays.asList("ignored"));
        when(mockConfig.getIgnored()).thenReturn(ignoderFiles);
        when(mockConfig.getFileTypes()).thenReturn(copyFiles);
        when(mockConfig.isKeepFolder()).thenReturn(true);

        fileHandler = new FileHandler();
    }

    @AfterEach
    void tearDown() {
        mockConfigClass.close();
        mockFiles.close();
        mockDirs.close();
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

    @Test
    public void testCreateFile() throws Throwable {
        final File path = new File("/my/path/");
        final File file = new File("korvus_maximus.txt");
        final File createdFile = fileHandler.createFile(path, file, false);
        assertEquals(new File(path, file.getName()).getAbsolutePath(), createdFile.getAbsolutePath());
    }

    @Test
    public void testCreateFileWithCreateFolderInBaseDir() throws Throwable {
        mockFiles.when(() -> Files.getNameWithoutExtension(anyString())).thenReturn("file_name");
        final File path = new File("/my/path/");
        final File file = new File("korvus_maximus.txt");
        final File createdFile = fileHandler.createFile(path, file, true);
        final File fullPath = new File("/my/path/file_name/");
        assertEquals(new File(fullPath, file.getName()).getAbsolutePath(), createdFile.getAbsolutePath());
    }

}
