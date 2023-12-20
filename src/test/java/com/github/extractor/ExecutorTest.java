package com.github.extractor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.github.extractor.candidate.FolderScanner;
import com.github.extractor.candidate.models.Candidate;
import com.github.extractor.configuration.Configuration;
import com.github.extractor.configuration.models.ConfigFolder;
import com.github.extractor.exceptions.FolderException;
import com.github.extractor.handlers.CopyHandler;
import com.github.extractor.handlers.RarHandler;
import com.github.extractor.utils.Dirs;

public class ExecutorTest {

    @Mock
    private Configuration mockConfig;
    @Mock
    private FolderScanner mockFolderScanner;

    private Executor executor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockConfig.isDryRun()).thenReturn(true);
        executor = new Executor(mockConfig, mockFolderScanner);
    }

    @Test
    void testConstructor() {
        new Executor();
    }

    @Test
    void runShouldInvokeScanAndCopyUnrarMethods() {
        final Executor spyExecutor = Mockito.spy(new Executor(mockConfig, mockFolderScanner));

        spyExecutor.run();

        verify(spyExecutor).scanForCandidates();
        verify(spyExecutor).copyAndUnrarCandidates();
    }

    @Test
    void scanForCandidatesShouldCallScanFoldersForEachConfigFolder() throws Exception {
        final List<ConfigFolder> folders = Arrays.asList(new ConfigFolder("input1", "output1"), new ConfigFolder("input2", "output2"));
        when(mockConfig.getFolders()).thenReturn(folders);

        executor.scanForCandidates();

        for (final ConfigFolder folder : folders) {
            verify(mockFolderScanner).scanFolders(eq(folder), eq(folder.getInputFolder()), eq(folder.getOutputFolder()));
        }
    }

    @Test
    void scanForCandidatesShouldHandleFolderException() throws FolderException {
        final Executor executorSpy = Mockito.spy(new Executor(mockConfig, mockFolderScanner));

        final List<ConfigFolder> folders = Collections.singletonList(new ConfigFolder("input", "output"));
        when(mockConfig.getFolders()).thenReturn(folders);

        // Mock the FolderScanner to throw FolderException
        doThrow(new FolderException("Test Exception")).when(mockFolderScanner).scanFolders(any(ConfigFolder.class), any(), any());

        executorSpy.scanForCandidates();

        // Verify that scanFolders was called
        verify(mockFolderScanner).scanFolders(any(ConfigFolder.class), any(), any());
        // Additional verification if necessary
    }

    @Test
    void copyAndUnrarCandidatesShouldProcessEachCandidate() {
        final List<Candidate> candidates = Arrays.asList(new Candidate("candidate1", new File("targetDir1")),
                new Candidate("candidate2", new File("targetDir2")));
        when(mockFolderScanner.getCandidates()).thenReturn(candidates);

        try (MockedStatic<Dirs> mockedDirs = Mockito.mockStatic(Dirs.class);
                MockedStatic<CopyHandler> mockedCopyHandler = Mockito.mockStatic(CopyHandler.class);
                MockedStatic<RarHandler> mockedRarHandler = Mockito.mockStatic(RarHandler.class)) {

            executor.copyAndUnrarCandidates();

            for (final Candidate candidate : candidates) {
                mockedDirs.verify(() -> Dirs.createDirs(candidate.targetDir), times(1));
                mockedCopyHandler.verify(() -> CopyHandler.copyFiles(candidate, mockConfig.isDryRun()), times(1));
                mockedRarHandler.verify(() -> RarHandler.unrarFiles(candidate, mockConfig.isDryRun()), times(1));
            }
        }
    }

    @Test
    void copyAndUnrarCandidateShouldHandleIOException() {
        final File targetDir = new File("targetDir");
        final Candidate candidate = new Candidate("candidate", new File("targetDir"));
        when(mockFolderScanner.getCandidates()).thenReturn(Collections.singletonList(candidate));
        when(mockConfig.isDryRun()).thenReturn(false);

        try (MockedStatic<Dirs> mockedDirs = Mockito.mockStatic(Dirs.class);
                MockedStatic<CopyHandler> mockedCopyHandler = Mockito.mockStatic(CopyHandler.class)) {
            final IOException exception = new IOException("Error");
            mockedCopyHandler.when(() -> Dirs.createDirs(targetDir)).thenThrow(exception);

            executor.copyAndUnrarCandidates();

            // Verify that exception is caught and handled (e.g., printed to System.out)
        }
    }

}
