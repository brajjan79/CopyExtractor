package com.github.extractor.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.models.StateConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import com.github.filesize.FileSize;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CopyHandlerTest {
    private Candidate candidate;
    private File sourceFile;
    private File targetFile;

    @BeforeEach
    void setUp() {
        sourceFile = new File("/path/to/source/file.txt");
        targetFile = new File("/path/to/target/file.txt");
        candidate = new Candidate("Name", new File("/path/to/target"));
        candidate.filesToCopy.add(sourceFile);
    }

    @Test
    void copyFilesShouldCopyWhenCanCopy() throws IOException, InterruptedException {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class);
                MockedStatic<FileSize> mockedFileSize = Mockito.mockStatic(FileSize.class);
                MockedStatic<StateConstants> mockedStateConstants = Mockito.mockStatic(StateConstants.class)) {

            mockedFileSize.when(() -> FileSize.size(any())).thenReturn(1000);
            mockedFiles.when(() -> Files.copy(any(), any())).thenReturn(null);

            final boolean result = CopyHandler.copyFiles(candidate, false);

            assertTrue(result);
            // mockedFiles.verify(() -> Files.copy(eq(sourceFile), eq(targetFile)));
            mockedStateConstants.verify(() -> StateConstants.addSuccess(), times(1));
        }
    }

    @Test
    void copyFilesShouldSkipWhenTargetExistsAndIsLarger() {
        try (MockedStatic<FileSize> mockedFileSize = Mockito.mockStatic(FileSize.class);
                MockedStatic<StateConstants> mockedStateConstants = Mockito.mockStatic(StateConstants.class)) {

            mockedFileSize.when(() -> FileSize.size(sourceFile).getBytes()).thenReturn(1000);
            mockedFileSize.when(() -> FileSize.size(targetFile).getBytes()).thenReturn(2000);

            final boolean result = CopyHandler.copyFiles(candidate, false);

            assertFalse(result);
            mockedStateConstants.verify(() -> StateConstants.addAlreadyExists(), times(1));
        }
    }

    @Test
    void copyFilesShouldHandleIOException() throws IOException, InterruptedException {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class);
                MockedStatic<FileSize> mockedFileSize = Mockito.mockStatic(FileSize.class);
                MockedStatic<StateConstants> mockedStateConstants = Mockito.mockStatic(StateConstants.class)) {

            mockedFileSize.when(() -> FileSize.size(any())).thenReturn(1000);
            mockedFiles.when(() -> Files.copy(any(), any())).thenThrow(new IOException("Test Exception"));

            final boolean result = CopyHandler.copyFiles(candidate, false);

            assertFalse(result);
            mockedStateConstants.verify(() -> StateConstants.addFailure(), times(1));
        }
    }

    @Test
    void copyFilesShouldNotCopyInDryRun() {
        final boolean result = CopyHandler.copyFiles(candidate, true);
        assertTrue(result); // Assuming success in dry run mode
    }
}
