package com.github.extractor.handlers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.extraction.ArchiveExtractor;
import com.github.extractor.extraction.ExtractionProgress;
import com.github.extractor.extraction.ExtractionResult;
import com.github.extractor.extraction.ProgressListener;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.StateConstants;

class UnrarHandlerTest {

    private Configuration config;
    private ArchiveExtractor archiveExtractor;
    private ProgressListener progressListener;
    private MockedStatic<StateConstants> stateConstants;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        config = mock(Configuration.class);
        archiveExtractor = mock(ArchiveExtractor.class);
        progressListener = mock(ProgressListener.class);
        stateConstants = mockStatic(StateConstants.class);
        candidate = new Candidate("candidate", new File("target"));
        candidate.filesToUnrar.add(new File("archive.rar"));
    }

    @AfterEach
    void tearDown() {
        stateConstants.close();
    }

    @Test
    void shouldDelegateExtractionAndRecordSuccess() {
        when(archiveExtractor.extract(any(File.class), any(File.class), any(ProgressListener.class)))
                .thenReturn(new ExtractionResult(ExtractionResult.Status.SUCCESS, 0, "", null));

        assertDoesNotThrow(() -> new UnrarHandler(config, archiveExtractor, progressListener).unrarFiles(candidate));

        verify(archiveExtractor).extract(candidate.filesToUnrar.get(0), candidate.targetDir, progressListener);
        stateConstants.verify(StateConstants::addSuccess);
    }

    @Test
    void shouldRecordArchiveFailure() {
        final IOException cause = new IOException("failed");
        when(archiveExtractor.getName()).thenReturn("Test extractor");
        when(archiveExtractor.extract(any(File.class), any(File.class), any(ProgressListener.class)))
                .thenReturn(new ExtractionResult(ExtractionResult.Status.ARCHIVE_ERROR, 2, "CRC error", cause));

        assertDoesNotThrow(() -> new UnrarHandler(config, archiveExtractor, progressListener).unrarFiles(candidate));

        stateConstants.verify(StateConstants::addFailure);
    }

    @Test
    void shouldNotExtractDuringDryRun() {
        when(config.isDryRun()).thenReturn(true);
        when(archiveExtractor.getName()).thenReturn("Test extractor");

        new UnrarHandler(config, archiveExtractor, progressListener).unrarFiles(candidate);

        verify(archiveExtractor, never()).extract(any(File.class), any(File.class), any(ProgressListener.class));
    }

    @Test
    void shouldSupportExistingConstructors() {
        final Configuration singleton = mock(Configuration.class);
        try (MockedStatic<Configuration> configuration = mockStatic(Configuration.class)) {
            configuration.when(Configuration::getInstance).thenReturn(singleton);
            assertNotNull(new UnrarHandler());
        }
        assertNotNull(new UnrarHandler(config));
    }
}
