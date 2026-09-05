package com.github.extractor.handlers;

import java.io.File;

import com.github.extractor.configuration.Configuration;
import com.github.extractor.extraction.ArchiveExtractor;
import com.github.extractor.extraction.ArchiveExtractorSelector;
import com.github.extractor.extraction.ConsoleProgressListener;
import com.github.extractor.extraction.ExtractionResult;
import com.github.extractor.extraction.ProgressListener;
import com.github.extractor.models.Candidate;
import com.github.extractor.models.StateConstants;

/**
 * Coordinates archive extraction without depending on a specific backend.
 */
public class UnrarHandler {

    private final Configuration config;
    private final ArchiveExtractor archiveExtractor;
    private final ProgressListener progressListener;

    public UnrarHandler() {
        this(Configuration.getInstance());
    }

    public UnrarHandler(final Configuration config) {
        this(config, new ArchiveExtractorSelector().select(config.getArchiveExtractor()), new ConsoleProgressListener());
    }

    public UnrarHandler(final Configuration config, final ArchiveExtractor archiveExtractor,
            final ProgressListener progressListener) {
        this.config = config;
        this.archiveExtractor = archiveExtractor;
        this.progressListener = progressListener;
    }

    public void unrarFiles(final Candidate candidate) {
        for (final File archive : candidate.filesToUnrar) {
            if (config.isDryRun()) {
                System.out.println("Should have extracted " + archive.getAbsolutePath() + " with "
                        + archiveExtractor.getName());
                continue;
            }

            final ExtractionResult result = archiveExtractor.extract(archive, candidate.targetDir, progressListener);
            if (result.isSuccess()) {
                StateConstants.addSuccess();
            } else {
                reportFailure(archive, result);
            }
        }
    }

    private void reportFailure(final File archive, final ExtractionResult result) {
        System.out.println("Failed to extract file with " + archiveExtractor.getName() + ": " + archive.getName());
        if (!result.output().isBlank()) {
            System.out.println(result.output());
        }
        if (result.cause() != null) {
            result.cause().printStackTrace();
        }
        StateConstants.addFailure();
    }
}
