package com.github.extractor.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

public class DirsTest {

    private File mockedDir;
    private File mockedSecondaryDir;

    @Before
    public void init() {
        mockedDir = PowerMockito.mock(File.class);
        mockedSecondaryDir = PowerMockito.mock(File.class);
    }

    /**
     * Just for 100% test coverage.
     */
    @Test
    public void testInit() {
        new Dirs();
    }

    @Test
    public void testGetBaseDirName() {
        final String fileName = "2014-14-16.jpg";
        when(mockedDir.getName()).thenReturn(fileName);

        final String groupByRegex = "-";
        final String actualName = Dirs.getBaseDirName(mockedDir, groupByRegex);

        final String expectedName = "2014";
        assertEquals(expectedName, actualName);
    }

    @Test
    public void testGetBaseDirNameNoMatch() {
        final String fileName = "2014-14-16.jpg";
        when(mockedDir.getName()).thenReturn(fileName);

        final String groupByRegex = "(?!x)x";
        final String actualName = Dirs.getBaseDirName(mockedDir, groupByRegex);

        assertTrue(actualName.isEmpty());
    }

    @Test
    public void testGetTargetDirName() {
        final String baseDir = "2014";
        final String fileName = "DCM";
        when(mockedDir.getName()).thenReturn(fileName);

        final String actualName = Dirs.getTargetDirName(mockedDir, baseDir);

        final String expectedName = "2014/DCM";
        assertEquals(expectedName, actualName);
    }

    @Test
    public void testGetTargetDirNameNoBaseDir() {
        final String baseDir = "";
        final String fileName = "DCM";
        when(mockedDir.getName()).thenReturn(fileName);

        final String actualName = Dirs.getTargetDirName(mockedDir, baseDir);

        final String expectedName = "DCM";
        assertEquals(expectedName, actualName);
    }

    @Test
    public void testLastModifiedLessThenIsTrue() {
        final int millisSinceLastModified = 10000;
        final long mockedLastModifiedTime = System.currentTimeMillis();
        when(mockedDir.lastModified()).thenReturn(mockedLastModifiedTime);

        final boolean actualName = Dirs.lastModifiedLessThen(mockedDir, millisSinceLastModified);

        assertTrue(actualName);
    }

    @Test
    public void testLastModifiedLessThenIsFalse() {
        final int millisSinceLastModified = 1000;
        final long mockedLastModifiedTime = System.currentTimeMillis() - 10000;
        when(mockedDir.lastModified()).thenReturn(mockedLastModifiedTime);

        final boolean actualName = Dirs.lastModifiedLessThen(mockedDir, millisSinceLastModified);

        assertFalse(actualName);
    }

    @Test
    public void testCreateAndDeleteDirs() throws Throwable {
        final File baseDir = new File("src/test/resources/folder_structure/delete_if_exist/");
        final File fullPath = new File("src/test/resources/folder_structure/delete_if_exist/level_1/level_2/level_3/level_4/level_5/");
        if (baseDir.exists()) {
            Dirs.deleteDirs(baseDir);
        }
        assertFalse("Directories to create should not exist before creating.", fullPath.exists());

        Dirs.createDirs(fullPath);
        assertTrue("Directories should have been created.", fullPath.exists());

        Dirs.deleteDirs(baseDir);
        assertFalse("Directories should have been deleted.", baseDir.exists());
    }

    @Test
    public void testCreateDirAlreadyExist() throws Throwable {
        when(mockedDir.exists()).thenReturn(true);
        when(mockedDir.isDirectory()).thenReturn(true);
        final boolean success = Dirs.createDirs(mockedDir);
        assertTrue("Dirs already exist", success);
    }

    @Test(expected = IOException.class)
    public void testCreateDirAlreadyExistButIsNotDirectory() throws Throwable {
        when(mockedDir.exists()).thenReturn(true);
        when(mockedDir.isDirectory()).thenReturn(false);
        Dirs.createDirs(mockedDir);
    }

    @Test
    public void testCreateDirFailed() throws Throwable {
        when(mockedDir.exists()).thenReturn(false);
        when(mockedDir.mkdir()).thenReturn(false);
        when(mockedDir.getParentFile()).thenReturn(mockedSecondaryDir);
        when(mockedSecondaryDir.exists()).thenReturn(true);
        final boolean success = Dirs.createDirs(mockedDir);
        assertFalse("Create dirs failed", success);
    }

    @Test
    public void testDeleteDirAlreadyDeleted() throws Throwable {
        when(mockedDir.exists()).thenReturn(false);
        final boolean success = Dirs.deleteDirs(mockedDir);
        assertTrue("Dirs already deleted", success);
    }

    @Test
    public void testDeleteDirIsFile() throws Throwable {
        when(mockedDir.exists()).thenReturn(true);
        when(mockedDir.isDirectory()).thenReturn(false);
        when(mockedDir.delete()).thenReturn(true);
        final boolean success = Dirs.deleteDirs(mockedDir);
        assertTrue("Dirs already deleted", success);
    }

    @Test
    public void testDeleteSubDirFailed() throws Throwable {
        when(mockedDir.exists()).thenReturn(true);
        when(mockedDir.isDirectory()).thenReturn(true);
        when(mockedSecondaryDir.isDirectory()).thenReturn(false);
        when(mockedSecondaryDir.delete()).thenReturn(false);
        final File[] fileList = new File[] { mockedSecondaryDir };
        when(mockedDir.listFiles()).thenReturn(fileList);
        final boolean success = Dirs.deleteDirs(mockedDir);
        assertFalse("Deletion of secondary file should fail", success);
    }

}
