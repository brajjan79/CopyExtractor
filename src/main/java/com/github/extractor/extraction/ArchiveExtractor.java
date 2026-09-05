package com.github.extractor.extraction;

import java.io.File;

/**
 * Extracts archives without exposing implementation-specific types.
 */
public interface ArchiveExtractor {

    String getName();

    boolean isAvailable();

    default ExtractionResult test(final File archive) {
        return test(archive, ProgressListener.NONE);
    }

    ExtractionResult test(File archive, ProgressListener progressListener);

    default ExtractionResult extract(final File archive, final File destination) {
        return extract(archive, destination, ProgressListener.NONE);
    }

    ExtractionResult extract(File archive, File destination, ProgressListener progressListener);
}
