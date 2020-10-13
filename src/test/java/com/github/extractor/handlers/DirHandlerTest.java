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
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.extractor.configuration.Configuration;

@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "com.github.extractor.handlers.*")
public class DirHandlerTest {

    private Configuration config;
    private DirHandler dirHandler;
    private File mockedDir;
    private FileHandler fileHandler;

    @Before
    public void init() {
        mockedDir = PowerMockito.mock(File.class);
        config = PowerMockito.mock(Configuration.class);
        fileHandler = PowerMockito.mock(FileHandler.class);
        dirHandler = new DirHandler(fileHandler, config);
    }

    @Test
    public void testDirToBeIncluded() throws Throwable {
        final List<String> includeList = new ArrayList<>(Arrays.asList("with"));
        when(config.getIncludedFolders()).thenReturn(includeList);
        final File dir = new File("src/test/resources/folder_structure/folder_with_file");
        final boolean result = dirHandler.directoryIncluded(dir);
        assertTrue("Expected directory " + dir.getName() + " to be included.", result);
    }

    @Test
    public void testDirNotToBeIncluded() throws Throwable {
        final File dir = new File("src/test/resources/folder_structure/folder_with_file");
        final boolean result = dirHandler.directoryIncluded(dir);
        assertFalse("Expected directory " + dir.getName() + " to be excluded.", result);
    }

    @Test
    public void testDirNotDirectory() throws Throwable {
        when(mockedDir.isDirectory()).thenReturn(false);
        final boolean result = dirHandler.directoryIncluded(mockedDir);
        assertFalse("A file should not be white listed as a directory.", result);
    }

    @Test
    public void testDirContainsIncludedFileTypes () {
        PowerMockito.when(fileHandler.isIgnored(Mockito.any())).thenReturn(false);
        PowerMockito.when(fileHandler.isIncludedFileType(Mockito.any())).thenReturn(true);
        final File folder = new File("src/test/resources/folder_structure/folder_with_file");
        final boolean result = dirHandler.dirContainsIncludedFileTypes(folder);
        assertTrue("File should be included", result);
    }

    @Test
    public void testDirDoesNotContainsIncludedFileTypes () {
        PowerMockito.when(fileHandler.isIgnored(Mockito.any())).thenReturn(false);
        PowerMockito.when(fileHandler.isIncludedFileType(Mockito.any())).thenReturn(false);
        final File folder = new File("src/test/resources/folder_structure/folder_with_file");
        final boolean result = dirHandler.dirContainsIncludedFileTypes(folder);
        assertFalse("File should not be included", result);

    }

    @Test
    public void tastNumberOfFoldersOfInterest () {
        PowerMockito.when(fileHandler.isIgnored(Mockito.any())).thenReturn(false);
        final File folder = new File("src/test/resources/folder_structure");
        final boolean result = dirHandler.folderHasMultipleFoldersToScan(folder);
        assertTrue("Dir should have interesting folders", result);
    }

    @Test
    public void tastNumberOfFoldersOfInterestFalse () {
        PowerMockito.when(fileHandler.isIgnored(Mockito.any())).thenReturn(false);
        final File folder = new File("src/test/resources/folder_structure/empty_folder");
        final boolean result = dirHandler.folderHasMultipleFoldersToScan(folder);
        assertFalse("Dir should not have interesting folders", result);
    }

}
