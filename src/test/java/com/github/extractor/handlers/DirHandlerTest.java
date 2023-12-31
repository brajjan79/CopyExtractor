package com.github.extractor.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.utils.Dirs;
import com.github.extractor.utils.Rars;

public class DirHandlerTest {

    private Configuration config;
    private DirHandler dirHandler;
    private FileHandler fileHandler;

    @BeforeEach
    public void init() {
        final List<String> includeList = new ArrayList<>(Arrays.asList("with", "include"));
        final List<String> ignore = new ArrayList<>(Arrays.asList("ignored_folder"));

        fileHandler = mock(FileHandler.class);
        config = mock(Configuration.class);
        when(config.getIncludeFolders()).thenReturn(includeList);
        when(config.getIgnored()).thenReturn(ignore);

        dirHandler = new DirHandler(config, fileHandler);
    }

    @Test
    public void testDirToBeIncluded() throws Throwable {
        final File mockedDir = mock(File.class);
        when(mockedDir.isDirectory()).thenReturn(true);
        when(mockedDir.getName()).thenReturn("included_name");
        final boolean result = dirHandler.directoryIncluded(mockedDir);
        assertTrue("Expected directory " + mockedDir.getName() + " to be included.", result);
    }

    @Test
    public void testDirNotToBeIncluded() throws Throwable {
        final File mockedDir = mock(File.class);
        when(mockedDir.isDirectory()).thenReturn(true);
        when(mockedDir.getName()).thenReturn("excluded_name");
        final boolean result = dirHandler.directoryIncluded(mockedDir);
        assertFalse("Expected directory " + mockedDir.getName() + " to be excluded.", result);
    }

    @Test
    public void testDirNotDirectory() throws Throwable {
        final File mockedFile = mock(File.class);
        when(mockedFile.isDirectory()).thenReturn(false);
        final boolean result = dirHandler.directoryIncluded(mockedFile);
        assertFalse("A file should not be included as a directory.", result);
    }

    @Test
    public void testDirContainsIncludedFileTypes() {
        final File mockedDir = mock(File.class);
        final File mockedSubDir = mock(File.class);
        when(mockedDir.listFiles()).thenReturn(new File[] { mockedSubDir });

        when(fileHandler.isIgnored(mockedSubDir)).thenReturn(false);
        when(fileHandler.isIncludedFileType(mockedSubDir)).thenReturn(true);

        final boolean result = dirHandler.dirContainsIncludedFileTypes(mockedDir);
        assertTrue("File should be included", result);

    }

    @Test
    public void testDirDoesNotContainsIncludedFileTypes() {
        final File mockedDir = mock(File.class);
        final File mockedSubDir = mock(File.class);
        when(mockedDir.listFiles()).thenReturn(new File[] { mockedSubDir });

        when(fileHandler.isIgnored(mockedSubDir)).thenReturn(false);
        when(fileHandler.isIncludedFileType(mockedSubDir)).thenReturn(false);

        final boolean result = dirHandler.dirContainsIncludedFileTypes(mockedDir);
        assertFalse("File should not be included", result);

    }


    @Test
    public void testDirContainsIncludedFileTypesButIsIgnored() {
        final File mockedDir = mock(File.class);
        final File mockedSubDir = mock(File.class);
        when(mockedDir.listFiles()).thenReturn(new File[] { mockedSubDir });

        when(fileHandler.isIgnored(mockedSubDir)).thenReturn(true);
        when(fileHandler.isIncludedFileType(mockedSubDir)).thenReturn(true);

        final boolean result = dirHandler.dirContainsIncludedFileTypes(mockedDir);
        assertFalse("File should not be included", result);

    }

    @Test
    public void testDirDoesNotContainsIncludedFileTypesAndIsIgnored() {
        final File mockedDir = mock(File.class);
        final File mockedSubDir = mock(File.class);
        when(mockedDir.listFiles()).thenReturn(new File[] { mockedSubDir });

        when(fileHandler.isIgnored(mockedSubDir)).thenReturn(true);
        when(fileHandler.isIncludedFileType(mockedSubDir)).thenReturn(false);

        final boolean result = dirHandler.dirContainsIncludedFileTypes(mockedDir);
        assertFalse("File should not be included", result);

    }

    @Test
    public void testFolderHasMultipleFoldersToScan() {
        try (MockedStatic<Rars> rarHandler = Mockito.mockStatic(Rars.class)) {
            final File folder = buildMockedFolderStructure(rarHandler);
            final boolean result = dirHandler.folderHasMultipleFoldersToScan(folder);
            assertTrue("Dir should have interesting folders", result);

        }
    }

    @Test
    public void testFolderHasMultipleFoldersToScanFalse() {
        final File mockedDir = mock(File.class);
        when(mockedDir.listFiles()).thenReturn(null);

        final boolean result = dirHandler.folderHasMultipleFoldersToScan(mockedDir);
        assertFalse("Dir should not have interesting folders", result);
    }

    @Test
    public void testisValidSubfolderDirIsFile() {
        final File mockDir = mock(File.class);

        when(mockDir.isDirectory()).thenReturn(false);

        final boolean result = dirHandler.isValidSubfolder(mockDir, mockDir);
        assertFalse("Dir is not dir, return false", result);
    }

    @Test
    public void testisValidSubfolderDirIsSameDir() {
        final File mockDir = mock(File.class);

        when(mockDir.isDirectory()).thenReturn(true);

        final boolean result = dirHandler.isValidSubfolder(mockDir, mockDir);
        assertFalse("Dir is same dir, return false", result);
    }

    @Test
    public void testisValidSubfolderDirIsValid() {
        final File mockDir = mock(File.class);
        final File mockDir2 = mock(File.class);

        when(mockDir.isDirectory()).thenReturn(true);

        final boolean result = dirHandler.isValidSubfolder(mockDir, mockDir2);
        assertTrue("Dir is valid, return true", result);
    }

    @Test
    public void testBuildFile() {
        final File dir = new File("/target/");
        final File file = new File("source/file_folder/");

        final File resultingFile = dirHandler.buildFile(dir, file);
        assertEquals(System.getProperty("file.separator") + "target", resultingFile.getParent());
        assertEquals("file_folder", resultingFile.getName());
    }

    @Test
    public void testBuildTargetSubDirFile() {
        when(config.isKeepFolder()).thenReturn(true);
        when(config.getGroupByRegex()).thenReturn("");

        final File dir = new File("/target/");
        final File file = new File("source/file_folder/");

        try (MockedStatic<Dirs> mockedDir = Mockito.mockStatic(Dirs.class)) {
            mockedDir.when(() -> {
                Dirs.getBaseDirName(file, "");
            }).thenReturn("file");
            mockedDir.when(() -> {
                Dirs.getTargetDirName(file, "file");
            }).thenReturn("file/file_folder");

            final File resultingFile = dirHandler.buildTargetSubdirFile(dir, file);
            assertEquals(
                    System.getProperty("file.separator") + "target" + System.getProperty("file.separator") + "file",
                    resultingFile.getParent());
            assertEquals("file_folder", resultingFile.getName());
        }
    }

    @Test
    public void testBuildTargetSubDirFileDontKeepFolder() {
        when(config.isKeepFolder()).thenReturn(false);
        when(config.getGroupByRegex()).thenReturn("");

        final File dir = new File("/target/");
        final File file = new File("source/file_folder/");

        try (MockedStatic<Dirs> mockedDir = Mockito.mockStatic(Dirs.class)) {
            mockedDir.when(() -> {Dirs.getBaseDirName(file, "");}).thenReturn("file");
            mockedDir.when(() -> {Dirs.getTargetDirName(file, "file");}).thenReturn("file/file_folder");

            final File resultingFile = dirHandler.buildTargetSubdirFile(dir, file);
            assertEquals(System.getProperty("file.separator") + "target", resultingFile.getParent());
            assertEquals("file", resultingFile.getName());
        }
    }

    @Test
    public void testbuildTargetBaseDirFile() {
        when(config.getGroupByRegex()).thenReturn("");

        final File dir = new File("/target/");
        final File file = new File("source/file_folder/");

        try (MockedStatic<Dirs> mockedDir = Mockito.mockStatic(Dirs.class)) {
            mockedDir.when(() -> {Dirs.getBaseDirName(file, "");}).thenReturn("file");

            final File resultingFile = dirHandler.buildTargetBaseDirFile(dir, file);
            assertEquals(System.getProperty("file.separator") + "target", resultingFile.getParent());
            assertEquals("file", resultingFile.getName());
        }
    }

    private File buildMockedFolderStructure(MockedStatic<Rars> rarHandler) {
        final File startDir = mock(File.class);

        final File subDirA = mock(File.class);
        final File subDirB = mock(File.class);
        final File ignoredSubDir = mock(File.class);
        final File includedSubDir = mock(File.class);
        final File mockedFile = mock(File.class);

        // setup file lists
        final File[] startDirFileList = new File[] { subDirA };
        final File[] subDirAFileList = new File[] { subDirB, ignoredSubDir, mockedFile, includedSubDir };

        // startDir setup
        when(startDir.isDirectory()).thenReturn(true);
        when(startDir.getAbsolutePath()).thenReturn("/input_dir/");
        when(startDir.getName()).thenReturn("input_dir");
        when(startDir.listFiles()).thenReturn(startDirFileList);

        // subDirA setup
        when(subDirA.isDirectory()).thenReturn(true);
        when(subDirA.getAbsolutePath()).thenReturn("/input_dir/sub_dir_a");
        when(subDirA.getName()).thenReturn("sub_dir_a");
        when(subDirA.listFiles()).thenReturn(subDirAFileList);
        when(fileHandler.isIgnored(subDirA)).thenReturn(false);
        rarHandler.when(() -> Rars.dirContainsUnrarable(subDirA)).thenReturn(false);

        // subDirB setup
        when(subDirB.isDirectory()).thenReturn(true);
        when(subDirB.getAbsolutePath()).thenReturn("/input_dir/sub_dir_a/sub_dir_b");
        when(subDirB.getName()).thenReturn("sub_dir_b");
        when(fileHandler.isIgnored(subDirB)).thenReturn(false);
        rarHandler.when(() -> Rars.dirContainsUnrarable(subDirB)).thenReturn(true);

        // ignoredSubdir setup
        when(ignoredSubDir.isDirectory()).thenReturn(true);
        when(ignoredSubDir.getAbsolutePath()).thenReturn("/input_dir/sub_dir_a/ignored_dir");
        when(ignoredSubDir.getName()).thenReturn("ignored_dir");
        when(fileHandler.isIgnored(ignoredSubDir)).thenReturn(true);
        rarHandler.when(() -> Rars.dirContainsUnrarable(ignoredSubDir)).thenReturn(true);

        // includedSubDir setup
        when(ignoredSubDir.isDirectory()).thenReturn(true);
        when(ignoredSubDir.getAbsolutePath()).thenReturn("/input_dir/sub_dir_a/included_dir");
        when(ignoredSubDir.getName()).thenReturn("included_dir");
        when(fileHandler.isIgnored(ignoredSubDir)).thenReturn(false);
        rarHandler.when(() -> Rars.dirContainsUnrarable(ignoredSubDir)).thenReturn(true);

        // mockedFile setup
        when(mockedFile.isFile()).thenReturn(true);
        when(mockedFile.isDirectory()).thenReturn(false);
        when(mockedFile.getAbsolutePath()).thenReturn("/something/dir_with_file/file.jpg");
        when(mockedFile.getName()).thenReturn("file1.jpg");

        return startDir;
    }
}
