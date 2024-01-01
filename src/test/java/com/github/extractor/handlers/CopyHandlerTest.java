package com.github.extractor.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.StateConstants;
import com.github.filesize.FileSize;

import java.io.File;
import java.io.IOException;
import com.google.common.io.Files;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CopyHandlerTest {

    @Mock
    private Configuration mockConfig;
    @Mock
    private FileHandler mockFileHandler;
    @Mock
    private File targetFile;

    private CopyHandler copyHandler;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        copyHandler = new CopyHandler(mockConfig, mockFileHandler);

        candidate = new Candidate("name", new File("targetDir"));
        candidate.filesToCopy.add(new File("sourceFile1.txt"));
        candidate.filesToCopy.add(new File("sourceFile2.txt"));

        when(mockFileHandler.createFile(any(), anyString())).thenReturn(targetFile);
    }

    @Test
    void copyFilesShouldCopyFilesCorrectly() throws IOException, InterruptedException {
        when(targetFile.exists()).thenReturn(true);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class);
                MockedStatic<FileSize> mockedFileSize = mockStatic(FileSize.class);
                MockedStatic<StateConstants> mockedStateConstants = mockStatic(StateConstants.class)) {

            mockedFileSize.when(() -> FileSize.getBytes(any(File.class))).thenReturn(1000.0);
            mockedFileSize.when(() -> FileSize.getBytes(targetFile)).thenReturn(900.0);
            when(mockConfig.isDryRun()).thenReturn(false);

            final boolean result = copyHandler.copyFiles(candidate);

            assertTrue(result);
            verify(mockFileHandler, times(candidate.filesToCopy.size())).createFile(eq(candidate.targetDir), anyString());
            mockedFiles.verify(() -> Files.copy(any(File.class), any(File.class)), times(candidate.filesToCopy.size()));
            mockedStateConstants.verify(() -> StateConstants.addSuccess(), times(candidate.filesToCopy.size()));
        }
    }

    @Test
    void copyFilesShouldCopyFilesCorrectlyTargetFileNoneExisting() throws IOException, InterruptedException {
        when(targetFile.exists()).thenReturn(false);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class);
                MockedStatic<FileSize> mockedFileSize = mockStatic(FileSize.class);
                MockedStatic<StateConstants> mockedStateConstants = mockStatic(StateConstants.class)) {

            mockedFileSize.when(() -> FileSize.getBytes(any(File.class))).thenReturn(1000.0);
            when(mockConfig.isDryRun()).thenReturn(false);

            final boolean result = copyHandler.copyFiles(candidate);

            assertTrue(result);
            verify(mockFileHandler, times(candidate.filesToCopy.size())).createFile(eq(candidate.targetDir), anyString());
            mockedFiles.verify(() -> Files.copy(any(File.class), any(File.class)), times(candidate.filesToCopy.size()));
            mockedStateConstants.verify(() -> StateConstants.addSuccess(), times(candidate.filesToCopy.size()));
        }
    }

    @Test
    void copyFilesShouldCopyFilesCorrectlyButIsDryRun() throws IOException, InterruptedException {
        when(targetFile.exists()).thenReturn(false);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class);
                MockedStatic<FileSize> mockedFileSize = mockStatic(FileSize.class);
                MockedStatic<StateConstants> mockedStateConstants = mockStatic(StateConstants.class)) {

            mockedFileSize.when(() -> FileSize.getBytes(any(File.class))).thenReturn(1000.0);
            when(mockConfig.isDryRun()).thenReturn(true);

            final boolean result = copyHandler.copyFiles(candidate);

            assertTrue(result);
            verify(mockFileHandler, times(candidate.filesToCopy.size())).createFile(eq(candidate.targetDir), anyString());
            mockedFiles.verify(() -> Files.copy(any(File.class), any(File.class)), times(0));
            mockedStateConstants.verify(() -> StateConstants.addSuccess(), times(candidate.filesToCopy.size()));
        }
    }

    @Test
    void copyFilesShouldCopyFilesButThrowException() throws IOException, InterruptedException {
        when(targetFile.exists()).thenReturn(false);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class);
                MockedStatic<FileSize> mockedFileSize = mockStatic(FileSize.class);
                MockedStatic<StateConstants> mockedStateConstants = mockStatic(StateConstants.class)) {

            mockedFileSize.when(() -> FileSize.getBytes(any(File.class))).thenReturn(1000.0);
            mockedFiles.when(() -> Files.copy(any(File.class), any(File.class))).thenThrow(new IOException());
            when(mockConfig.isDryRun()).thenReturn(false);

            final boolean result = copyHandler.copyFiles(candidate);

            assertFalse(result);
            verify(mockFileHandler, times(candidate.filesToCopy.size())).createFile(eq(candidate.targetDir), anyString());
            mockedFiles.verify(() -> Files.copy(any(File.class), any(File.class)), times(candidate.filesToCopy.size()));
            mockedStateConstants.verify(() -> StateConstants.addFailure(), times(candidate.filesToCopy.size()));
        }
    }

    @Test
    void copyFilesShouldHandleFileAlreadyExistsScenario() {
        when(targetFile.exists()).thenReturn(true);

        try (MockedStatic<FileSize> mockedFileSize = mockStatic(FileSize.class);
                MockedStatic<StateConstants> mockedStateConstants = mockStatic(StateConstants.class)) {

            mockedFileSize.when(() -> FileSize.getBytes(any(File.class))).thenReturn(1000.0);

            final boolean result = copyHandler.copyFiles(candidate);

            assertFalse(result);
            mockedStateConstants.verify(() -> StateConstants.addAlreadyExists(), times(candidate.filesToCopy.size()));
        }
    }

    // Additional test methods for covering other scenarios like IOException, InterruptedException, etc.
}
