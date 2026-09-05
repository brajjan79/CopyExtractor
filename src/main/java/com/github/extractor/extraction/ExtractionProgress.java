package com.github.extractor.extraction;

import java.util.Objects;

/**
 * A backend-independent extraction progress event. Percent and current file are
 * optional because not all extractors provide them reliably.
 */
public record ExtractionProgress(ExtractionPhase phase, Integer percent, String currentFile, String message) {

    public ExtractionProgress {
        Objects.requireNonNull(phase);
        if (percent != null && (percent < 0 || percent > 100)) {
            throw new IllegalArgumentException("Percent must be between 0 and 100");
        }
    }

    public static ExtractionProgress phase(final ExtractionPhase phase, final String message) {
        return new ExtractionProgress(phase, null, null, message);
    }
}
