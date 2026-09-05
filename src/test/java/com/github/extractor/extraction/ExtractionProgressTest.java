package com.github.extractor.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ExtractionProgressTest {

    @Test
    void shouldCreatePhaseWithoutOptionalDetails() {
        final ExtractionProgress progress = ExtractionProgress.phase(ExtractionPhase.TESTING, "Testing...");

        assertEquals(ExtractionPhase.TESTING, progress.phase());
        assertEquals("Testing...", progress.message());
        assertNull(progress.percent());
        assertNull(progress.currentFile());
    }

    @Test
    void shouldAcceptValidPercent() {
        final ExtractionProgress progress = new ExtractionProgress(ExtractionPhase.EXTRACTING, 50, "file.txt", null);

        assertEquals(50, progress.percent());
    }

    @Test
    void shouldRejectInvalidPercent() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExtractionProgress(ExtractionPhase.EXTRACTING, 101, null, null));
    }
}
