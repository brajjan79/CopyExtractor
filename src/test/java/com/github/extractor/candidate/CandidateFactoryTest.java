package com.github.extractor.candidate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.handlers.DirHandler;
import com.github.extractor.handlers.FileHandler;
import com.github.extractor.handlers.RarHandler;
import com.github.extractor.utils.Dirs;

public class CandidateFactoryTest {

    private static final int LAST_MODIFIED_WAIT_TIME = 10000;
    private DirHandler dirHandler;
    private FileHandler fileHandler;
    private CandidateFactory candidateFactory;
    private File rarFile_1;
    private File rarFile_2;
    private File rarFile_3;
    private File subFolder;
    private File sourceDir;
    private File targetDir;
    private File photoFile_1;
    private File photoFile_2;
    private File photoFile_3;

    @BeforeEach
    public void init() throws Exception {
        dirHandler = mock(DirHandler.class);
        fileHandler = mock(FileHandler.class);
        candidateFactory = new CandidateFactory(dirHandler, fileHandler);
    }

    /*
     * createCandidate tests
     */

    @Test
    public void testCreateCandidateWithRarFilesNoSubdirectories() throws Throwable {
        try (MockedStatic<RarHandler> rarHandler = Mockito.mockStatic(RarHandler.class);
                MockedStatic<FileHandler> fileHandler = Mockito.mockStatic(FileHandler.class);
                MockedStatic<Dirs> dirs = Mockito.mockStatic(Dirs.class)) {
            setupMockFolderFolders(rarHandler, dirs);
            when(dirHandler.directoryIncluded(Mockito.any())).thenReturn(false);

            final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
            assertEquals("Number of rar files should be 2.", 2, candidate.filesToUnrar.size());
            assertEquals("Compare rarfile name.", rarFile_1.getName(), candidate.filesToUnrar.get(0).getName());
            assertEquals("Compare rarfile name.", rarFile_2.getName(), candidate.filesToUnrar.get(1).getName());
            assertTrue(candidate.filesToCopy.isEmpty());
        }
    }

    @Test
    public void testCreateCandidateWithRarFilesWithSubdirectories() throws Throwable {
        try (MockedStatic<RarHandler> rarHandler = Mockito.mockStatic(RarHandler.class);
                MockedStatic<FileHandler> fileHandler = Mockito.mockStatic(FileHandler.class);
                MockedStatic<Dirs> dirs = Mockito.mockStatic(Dirs.class)) {
            setupMockFolderFolders(rarHandler, dirs);
            when(dirHandler.directoryIncluded(subFolder)).thenReturn(true);

            final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
            assertEquals("Number of rar files should be 3.", 3, candidate.filesToUnrar.size());
            assertEquals("Compare rarfile name.", rarFile_1.getName(), candidate.filesToUnrar.get(0).getName());
            assertEquals("Compare rarfile name.", rarFile_2.getName(), candidate.filesToUnrar.get(1).getName());
            assertEquals("Compare rarfile name.", rarFile_3.getName(), candidate.filesToUnrar.get(2).getName());
            assertTrue(candidate.filesToCopy.isEmpty());
        }
    }

    @Test
    public void testRarsFilesIgnored() throws Throwable {
        try (MockedStatic<RarHandler> rarHandler = Mockito.mockStatic(RarHandler.class);
                MockedStatic<Dirs> dirs = Mockito.mockStatic(Dirs.class)) {
            setupMockFolderFolders(rarHandler, dirs);
            when(dirHandler.directoryIncluded(subFolder)).thenReturn(true);
            when(fileHandler.isIgnored(Mockito.any())).thenReturn(true);

            final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
            assertTrue("Candidate.isEmpty() should be true when there is no content.", candidate.isEmpty());
        }
    }

    @Test
    public void testCreateCandidateWithFilesNoSubdirectories() throws Throwable {
        try (MockedStatic<RarHandler> rarHandler = Mockito.mockStatic(RarHandler.class);
                MockedStatic<Dirs> dirs = Mockito.mockStatic(Dirs.class)) {
            setupMockFolderFolders(rarHandler, dirs);
            when(dirHandler.directoryIncluded(Mockito.any())).thenReturn(false);
            when(fileHandler.isIgnored(Mockito.any())).thenReturn(false);

            when(fileHandler.isIncludedFileType(photoFile_1)).thenReturn(true);
            when(fileHandler.isIncludedFileType(photoFile_2)).thenReturn(true);

            final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
            assertEquals("Number of .png files should be 2.", 2, candidate.filesToCopy.size());
        }
    }

    @Test
    public void testCreateCandidateWithFilesWithSubdirectories() throws Throwable {
        try (MockedStatic<RarHandler> rarHandler = Mockito.mockStatic(RarHandler.class);
                MockedStatic<Dirs> dirs = Mockito.mockStatic(Dirs.class)) {
            setupMockFolderFolders(rarHandler, dirs);
            when(dirHandler.directoryIncluded(subFolder)).thenReturn(true);
            when(fileHandler.isIgnored(Mockito.any())).thenReturn(false);

            when(fileHandler.isIncludedFileType(photoFile_1)).thenReturn(true);
            when(fileHandler.isIncludedFileType(photoFile_2)).thenReturn(true);
            when(fileHandler.isIncludedFileType(photoFile_3)).thenReturn(true);

            final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
            assertEquals("Number of .png files should be 3.", 3, candidate.filesToCopy.size());
        }
    }

    @Test
    public void testNoRarsAllFilesIgnored() throws Throwable {
        try (MockedStatic<RarHandler> rarHandler = Mockito.mockStatic(RarHandler.class);
                MockedStatic<Dirs> dirs = Mockito.mockStatic(Dirs.class)) {
            setupMockFolderFolders(rarHandler, dirs);
            when(fileHandler.isIgnored(Mockito.any())).thenReturn(true);
            when(fileHandler.isIncludedFileType(Mockito.any())).thenReturn(true);
            when(dirHandler.directoryIncluded(Mockito.any())).thenReturn(false);

            final File[] folderList = new File[] { photoFile_1, photoFile_2 };
            when(sourceDir.listFiles()).thenReturn(folderList);

            final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
            assertTrue("Candidate.isEmpty() should be true when there is no content.", candidate.isEmpty());
        }

    }

    @Test
    public void testSubdirRecentlyModified() throws Throwable {
        try (MockedStatic<RarHandler> rarHandler = Mockito.mockStatic(RarHandler.class);
                MockedStatic<Dirs> dirs = Mockito.mockStatic(Dirs.class)) {
            setupMockFolderFolders(rarHandler, dirs);
            dirs.when(() -> {
                Dirs.lastModifiedLessThen(sourceDir, 10000);
            }).thenReturn(true);
            final Candidate candidate = candidateFactory.createCandidate(sourceDir, targetDir);
            assertTrue("Candidate.isEmpty() should be true when there is no content.", candidate.isEmpty());
        }
    }

    private void setupMockFolderFolders(MockedStatic<RarHandler> rarHandler, MockedStatic<Dirs> dirs) throws Exception {
        subFolder = mock(File.class);
        sourceDir = mock(File.class);
        targetDir = mock(File.class);

        rarFile_1 = new File("/some_folder/rar1.rar");
        rarFile_2 = new File("/some_folder/rar2.rar");
        rarFile_3 = new File("/some_folder/subFolder/rar3.rar");
        photoFile_1 = new File("/some_folder/photo3.png");
        photoFile_2 = new File("/some_folder/photo3.png");
        photoFile_3 = new File("/some_folder/subFolder/photo3.png");

        final File[] subFolderList = new File[] { rarFile_3, photoFile_3 };
        final File[] rarFolderList = new File[] { rarFile_1, rarFile_2, subFolder, photoFile_1, photoFile_2 };

        when(sourceDir.listFiles()).thenReturn(rarFolderList);
        when(subFolder.getPath()).thenReturn("/some_folder/subFolder/");
        when(subFolder.listFiles()).thenReturn(subFolderList);

        rarHandler.when(() -> RarHandler.fileIsUnrarable(subFolder)).thenReturn(false);
        rarHandler.when(() -> RarHandler.fileIsUnrarable(sourceDir)).thenReturn(false);

        rarHandler.when(() -> RarHandler.fileIsUnrarable(photoFile_1)).thenReturn(false);
        rarHandler.when(() -> RarHandler.fileIsUnrarable(photoFile_2)).thenReturn(false);
        rarHandler.when(() -> RarHandler.fileIsUnrarable(photoFile_3)).thenReturn(false);

        rarHandler.when(() -> RarHandler.fileIsUnrarable(rarFile_1)).thenReturn(true);
        rarHandler.when(() -> RarHandler.fileIsUnrarable(rarFile_2)).thenReturn(true);
        rarHandler.when(() -> RarHandler.fileIsUnrarable(rarFile_3)).thenReturn(true);

        dirs.when(() -> Dirs.lastModifiedLessThen(sourceDir, LAST_MODIFIED_WAIT_TIME)).thenReturn(false);
    }

}
