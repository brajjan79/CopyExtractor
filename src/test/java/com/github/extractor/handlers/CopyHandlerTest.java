package com.github.extractor.handlers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.StateConstants;
import com.github.extractor.utils.FileProgressBar;
import com.github.extractor.utils.PathShortener;
import com.github.filesize.FileSize;
import com.google.common.io.Files;

class CopyHandlerTest {

    @Mock
    private Configuration mockConfig;
    @Mock
    private FileHandler mockFileHandler;
    @Mock
    private File targetFile;
    @Mock
    private FileProgressBar mockedFileProgressBar;

    private MockedStatic<FileSize> mockedFileSize;
    private MockedStatic<Files> mockedFiles;
    private MockedStatic<FileProgressBar> mockedFileProgressBarClass;
    private MockedStatic<StateConstants> mockedStateConstants;
    private MockedStatic<PathShortener> mockPathShortener;

    private CopyHandler copyHandler;
    private Candidate candidate;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        mockedFileSize = mockStatic(FileSize.class);
        mockedFiles = mockStatic(Files.class);
        mockedFileProgressBarClass = mockStatic(FileProgressBar.class);
        mockedStateConstants = mockStatic(StateConstants.class);
        mockPathShortener = mockStatic(PathShortener.class);

        mockedFileProgressBarClass.when(() -> FileProgressBar.build()).thenReturn(mockedFileProgressBar);
        when(mockedFileProgressBar.expectedSize(anyDouble())).thenReturn(mockedFileProgressBar);
        when(mockedFileProgressBar.trackedFile(any())).thenReturn(mockedFileProgressBar);
        when(mockedFileProgressBar.setAction(any())).thenReturn(mockedFileProgressBar);

        mockedFileSize.when(() -> FileSize.getBytes(any(File.class))).thenReturn(1000.0);

        when(mockConfig.isDryRun()).thenReturn(false);

        when(mockFileHandler.createFile(any(File.class), any(File.class), eq(false))).thenReturn(targetFile);

        copyHandler = new CopyHandler(mockConfig, mockFileHandler);

        candidate = new Candidate("name", new File("targetDir"));
        candidate.filesToCopy.add(new File("sourceFile1.txt"));
        candidate.filesToCopy.add(new File("sourceFile2.txt"));
    }

    @AfterEach
    void tearDown() {
        mockedFileSize.close();
        mockedFiles.close();
        mockedFileProgressBarClass.close();
        mockedStateConstants.close();
        mockPathShortener.close();
    }

    @Test
    void copyFilesShouldCopyFilesCorrectly() throws Throwable {
        when(targetFile.exists()).thenReturn(true);
        mockedFileSize.when(() -> FileSize.getBytes(targetFile)).thenReturn(900.0);

        final boolean result = copyHandler.copyFiles(candidate);

        assertTrue(result);
        verify(mockFileHandler, times(candidate.filesToCopy.size())).createFile(eq(candidate.targetDir), any(), eq(false));
        mockedFiles.verify(() -> Files.copy(any(File.class), any(File.class)), times(candidate.filesToCopy.size()));
        mockedStateConstants.verify(() -> StateConstants.addSuccess(), times(candidate.filesToCopy.size()));
    }

    @Test
    void copyFilesShouldCopyFilesCorrectlyTargetFileNoneExisting() throws Throwable {
        when(targetFile.exists()).thenReturn(false);

        final boolean result = copyHandler.copyFiles(candidate);

        assertTrue(result);
        verify(mockFileHandler, times(candidate.filesToCopy.size())).createFile(eq(candidate.targetDir), any(), eq(false));
        mockedFiles.verify(() -> Files.copy(any(File.class), any(File.class)), times(candidate.filesToCopy.size()));
        mockedStateConstants.verify(() -> StateConstants.addSuccess(), times(candidate.filesToCopy.size()));
    }

    @Test
    void copyFilesShouldCopyFilesCorrectlyButIsDryRun() throws Throwable {
        when(targetFile.exists()).thenReturn(false);
        when(mockConfig.isDryRun()).thenReturn(true);

        final boolean result = copyHandler.copyFiles(candidate);

        assertTrue(result);
        verify(mockFileHandler, times(candidate.filesToCopy.size())).createFile(eq(candidate.targetDir), any(), eq(false));
        mockedFiles.verify(() -> Files.copy(any(File.class), any(File.class)), times(0));
        mockedStateConstants.verify(() -> StateConstants.addSuccess(), times(candidate.filesToCopy.size()));
    }

    @Test
    void copyFilesShouldCopyFilesButThrowException() throws Throwable {
        when(targetFile.exists()).thenReturn(false);
        mockedFiles.when(() -> Files.copy(any(File.class), any(File.class))).thenThrow(new IOException());

        final boolean result = copyHandler.copyFiles(candidate);

        assertFalse(result);
        verify(mockFileHandler, times(candidate.filesToCopy.size())).createFile(eq(candidate.targetDir), any(), eq(false));
        mockedFiles.verify(() -> Files.copy(any(File.class), any(File.class)), times(candidate.filesToCopy.size()));
        mockedStateConstants.verify(() -> StateConstants.addFailure(), times(candidate.filesToCopy.size()));

    }

    @Test
    void copyFilesShouldHandleFileAlreadyExistsScenario() {
        when(targetFile.exists()).thenReturn(true);

        final boolean result = copyHandler.copyFiles(candidate);

        assertFalse(result);
        mockedStateConstants.verify(() -> StateConstants.addAlreadyExists(), times(candidate.filesToCopy.size()));
    }


    // Additional test methods for covering other scenarios like IOException, InterruptedException, etc.
}
