package com.github.extractor;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.github.extractor.candidate.FolderScanner;
import com.github.extractor.configuration.Configuration;
import com.github.extractor.exceptions.FolderException;
import com.github.extractor.handlers.CopyHandler;
import com.github.extractor.handlers.UnrarHandler;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.ConfigFolder;
import com.github.extractor.utils.Dirs;

public class ExecutorTest {

    @Mock
    private Configuration mockConfig;
    @Mock
    private FolderScanner mockFolderScanner;
    @Mock
    private CopyHandler copyHandler;
    @Mock
    private UnrarHandler unrarHandler;

    private MockedStatic<Dirs> dirs;

    private Executor executor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dirs = mockStatic(Dirs.class);

        when(mockConfig.isDryRun()).thenReturn(true);
        executor = new Executor(mockConfig, mockFolderScanner, copyHandler, unrarHandler);
    }

    @AfterEach
    void tearDown() {
        dirs.close();
    }

    /**
     * Just for 100% test coverage.
     */
    @Test
    public void testInit() {
        try {
            new Executor();
        } catch (final Exception e) {
            fail("Failed to initiate");
        }
    }

    @Test
    void runShouldInvokeScanAndCopyUnrarMethods() {
        final Executor spyExecutor = Mockito.spy(new Executor(mockConfig, mockFolderScanner, copyHandler, unrarHandler));

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
            verify(mockFolderScanner).scanFolders(eq(folder));
        }
    }

    @Test
    void scanForCandidatesShouldHandleFolderException() throws FolderException {
        final Executor executorSpy = Mockito.spy(new Executor(mockConfig, mockFolderScanner, copyHandler, unrarHandler));

        final List<ConfigFolder> folders = Collections.singletonList(new ConfigFolder("input", "output"));
        when(mockConfig.getFolders()).thenReturn(folders);

        // Mock the FolderScanner to throw FolderException
        doThrow(new FolderException("Test Exception")).when(mockFolderScanner).scanFolders(any(ConfigFolder.class));

        executorSpy.scanForCandidates();

        // Verify that scanFolders was called
        verify(mockFolderScanner).scanFolders(any(ConfigFolder.class));
        // Additional verification if necessary
    }

    @Test
    void copyAndUnrarCandidatesShouldProcessEachCandidate() {
        final List<Candidate> candidates = Arrays.asList(new Candidate("candidate1", new File("targetDir1")),
                new Candidate("candidate2", new File("targetDir2")));
        when(mockFolderScanner.getCandidates()).thenReturn(candidates);

        executor.copyAndUnrarCandidates();

        for (final Candidate candidate : candidates) {
            verify(copyHandler).copyFiles(candidate);
            verify(unrarHandler).unrarFiles(candidate);
        }
    }

}
